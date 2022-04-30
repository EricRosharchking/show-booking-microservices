package com.liyuan.hong.showbooking.rest.controller;

import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.liyuan.hong.showbooking.domain.ShowDto;
import com.liyuan.hong.showbooking.rest.service.ShowService;

@RestController
@RequestMapping(path = "/shows")
public class ShowRestController {
	Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	ShowService showService;

	public ShowRestController() {

	}

	public ShowService getShowService() {
		return showService;
	}

	public void setShowService(ShowService showService) {
		this.showService = showService;
	}

	@PostMapping(path = "/setup")
	@ResponseStatus(HttpStatus.CREATED)
	public Boolean setupShow(@RequestBody ShowDto showDto) {
		logger.info("Received incoming request to setup show: " + showDto.toString());
		try {
			showService.setupShow(showDto.getId(), showDto.getRows(), showDto.getRows(), showDto.getCancelWindow());
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	@GetMapping(path = "/{showId}/view")
	@ResponseStatus(HttpStatus.OK)
	public void viewShow(@PathVariable(value = "showId") long showId) {
		showService.viewShow(showId);
	}

	@PatchMapping(path = "/{showId}/removeSeats")
	public Boolean removeSeatsFromShow(@PathVariable(value = "showId") long showId,
			@RequestParam(value = "seats") int seatsToRemove) {
		return showService.removeSeatsFromShow(showId, seatsToRemove);
	}

	@PatchMapping(path = "/{showId}/addRows")
	public Boolean addRowsToShow(@PathVariable(value = "showId") long showId,
			@RequestParam(value = "seats") int rowsToAdd) {
		return showService.addRowsToShow(showId, rowsToAdd);
	}

	@GetMapping(path = "/{showId}/availability")
	public void checkAvailability(@PathVariable(value = "showId") long showId) {

	}
}
