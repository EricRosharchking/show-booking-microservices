package com.liyuan.hong.showbooking.rest.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.liyuan.hong.showbooking.domain.ShowDto;
import com.liyuan.hong.showbooking.rest.helper.DtoHelper;
import com.liyuan.hong.showbooking.rest.service.ShowService;

@RestController
@RequestMapping(path = "/show/{showNum}")
public class ShowRestController {

	Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	ShowService showService;
	@Autowired
	DtoHelper dtoHelper;

	public ShowRestController() {

	}

	@PostMapping(path = "/setup", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Boolean> setupShow(@RequestBody ShowDto showDto) {
		logger.printf(Level.INFO, "Received incoming request to setup show: %s%n", showDto.toString());
		ResponseEntity<Boolean> response = ResponseEntity.created(null).body(true);
		try {
			showService.setupShow(showDto.getId(), showDto.getRows(), showDto.getSeats(), showDto.getCancelWindow());
		} catch (IllegalStateException e) {
			logger.error(e);
			response = ResponseEntity.badRequest().header("reasonOfFailure", e.getMessage()).body(false);
		}catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.internalServerError().build();
		}
		logger.info("Request to setup show: " + showDto.toString() + " completed");
		return response;
	}

	@PostMapping(path = "/removeSeats")
	public ResponseEntity<String> removeSeatsFromShow(@PathVariable(value = "showNum") long showId,
			@RequestParam(value = "seats") int seatsToRemove) {
		logger.printf(Level.INFO, "Received incoming request to remove [%d] seats from show: [%d]%n", seatsToRemove,
				showId);
		ResponseEntity<String> response = null;
		String result = "Failed";
		try {
			boolean status = showService.removeSeatsFromShow(showId, seatsToRemove);
			result = status ? "Successfully removed " + seatsToRemove + " seats from Show of Id " + showId
					: result + ", not enough seats to remove";
			response = ResponseEntity.ok(result);
		} catch (IllegalStateException e) {
			response = ResponseEntity.badRequest().header("reasonOfFailure", e.getMessage()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.internalServerError().build();
		}
		logger.printf(Level.INFO, "%s.%n", result);
		return response;
	}

	@PostMapping(path = "/addRows")
	public ResponseEntity<Boolean> addRowsToShow(@PathVariable(value = "showNum") long showId,
			@RequestParam(value = "rows") int rowsToAdd) {
		logger.printf(Level.INFO, "Received incoming request to add [%d] rows to show: [%d]%n", rowsToAdd, showId);
		ResponseEntity<Boolean> response = null;
		try {
			response = ResponseEntity.ok().body(showService.addRowsToShow(showId, rowsToAdd));
		} catch (IllegalStateException e) {
			response = ResponseEntity.badRequest().header("reasonOfFailure", e.getMessage()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.internalServerError().build();
		}
		logger.printf(Level.INFO, "Completed incoming request to add rows to show%n");
		return response;
	}

	@GetMapping(path = "/availability")
	public ResponseEntity<String[]> checkShowAvailability(@PathVariable(value = "showNum") long showId) {
		logger.printf(Level.INFO, "Received incoming request to check availability for show: [%d]%n", showId);
		ResponseEntity<String[]> response = ResponseEntity.noContent().build();
		try {
			String[] result = showService.seatsAvailablilityOfShow(showId).toArray(String[]::new);
			if (result.length > 0) {
				response = ResponseEntity.ok().body(result);
			}
		} catch (IllegalStateException e) {
			
			response = ResponseEntity.badRequest().header("reasonOfFailure", e.getMessage()).build();
		} catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.internalServerError().build();
		}
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
