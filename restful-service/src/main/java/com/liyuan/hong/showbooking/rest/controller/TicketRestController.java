package com.liyuan.hong.showbooking.rest.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liyuan.hong.showbooking.domain.BookingDto;
import com.liyuan.hong.showbooking.domain.TicketDto;
import com.liyuan.hong.showbooking.rest.helper.DtoHelper;
import com.liyuan.hong.showbooking.rest.service.ShowService;
import com.liyuan.hong.showbooking.rest.service.TicketService;

@RestController
@RequestMapping(path = "/ticket/{showNum}")
public class TicketRestController {

	Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	ShowService showService;
	@Autowired
	TicketService ticketService;
	@Autowired
	DtoHelper dtoHelper;

	public TicketRestController() {

	}

	@GetMapping(path = "/view", produces = "application/json")
	public ResponseEntity<TicketDto[]> viewTicketsOfShow(@PathVariable(value = "showNum") long showId) {
		logger.printf(Level.INFO, "Received incoming request to view booked tickets for show: [%d]%n", showId);
		ResponseEntity<TicketDto[]> response = ResponseEntity.noContent().build();
		try {
			TicketDto[] ticketDtos = ticketService.viewShow(showId).stream()
					.map(t -> dtoHelper.prepareTicketDtoFromTicket(t)).toArray(TicketDto[]::new);
			if (ticketDtos.length > 0) {
				response = ResponseEntity.ok().body(ticketDtos);
			}
		} catch (IllegalStateException e) {
			response = ResponseEntity.badRequest().header("reasonOfFailure", e.getMessage()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.internalServerError().build();
		}

		return response;
	}

	@PostMapping(path = "/book", consumes = "application/json")
	public ResponseEntity<Long> bookTicket(@PathVariable(value = "showNum") long showId,
			@RequestBody BookingDto bookingDto) {
		logger.printf(Level.INFO,
				"Received incoming request to book ticket [%s] for show: [%d] with phoneNumber [%s]%n",
				bookingDto.getCsSeats(), showId, bookingDto.getPhoneNum());
		ResponseEntity<Long> response = ResponseEntity.noContent().build();
		try {
			response = ResponseEntity.ok()
					.body(ticketService.bookTicket(showId, bookingDto.getPhoneNum(), bookingDto.getCsSeats()).getId());
		} catch (NullPointerException e) {
			response = ResponseEntity.badRequest()
					.header("reasonOfFailure", "One (or more) seat is not available, please check and try again")
					.build();
		} catch (DataIntegrityViolationException e) {
			response = ResponseEntity.badRequest()
					.header("reasonOfFailure", "Only one booking per phone number is allowed per show").build();
		} catch (IllegalStateException e) {
			response = ResponseEntity.badRequest().header("reasonOfFailure", e.getMessage()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.internalServerError().build();
		}
		logger.printf(Level.INFO, "Completed incoming request to book ticket%n");
		return response;
	}

	@DeleteMapping(path = "/cancel")
	public ResponseEntity<Boolean> cancelTicket(@PathVariable(value = "showNum") long showId,
			@RequestParam(value = "ticketNum") long ticketId, @RequestParam(value = "phoneNum") String phoneNum) {
		logger.printf(Level.INFO,
				"Received incoming request to cancel ticket [%d] for show: [%d] with phoneNumber [%s]%n", ticketId,
				showId, phoneNum);
		ResponseEntity<Boolean> response = ResponseEntity.noContent().build();
		try {
			ticketService.cancelTicket(showId, ticketId, phoneNum);
			response = ResponseEntity.ok(true);
		} catch (DataIntegrityViolationException e) {
			response = ResponseEntity.badRequest()
					.header("reasonOfFailure", "Only one booking per phone number is allowed per show").build();
		} catch (IllegalStateException e) {
			response = ResponseEntity.badRequest().header("reasonOfFailure", e.getMessage()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.internalServerError().build();
		}
		logger.printf(Level.INFO, "Completed incoming request to cancel ticket%n");
		return response;
	}

	public ShowService getShowService() {
		return showService;
	}

	public void setShowService(ShowService showService) {
		this.showService = showService;
	}

	public DtoHelper getDtoHelper() {
		return dtoHelper;
	}

	public void setDtoHelper(DtoHelper dtoHelper) {
		this.dtoHelper = dtoHelper;
	}
}
