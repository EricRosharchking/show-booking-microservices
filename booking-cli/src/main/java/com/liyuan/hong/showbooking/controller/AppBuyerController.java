package com.liyuan.hong.showbooking.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.liyuan.hong.showbooking.domain.BookingDto;
import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

import net.minidev.json.JSONObject;

public class AppBuyerController extends AppController {

	private final String END_POINT = "http://localhost:8080/show/";
	Logger logger = LogManager.getLogger(AppAdminController.class);

	private RestTemplate restTemplate;
	private HttpHeaders headers;

	@Autowired
	public AppBuyerController(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
		logger.debug("RestTemplate Built");
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		logger.debug("HttpHeaders set");
	}

	/**
	 * Availability <Show Number> (List all available seat numbers for a show. E.g
	 * A1, F4 etc)
	 * 
	 * @param showId
	 * @throws AdminException
	 */
	@Override
	public void checkShowAvailability(long showId) throws AdminException {
		logger.debug("Check Show Availability");
		ResponseEntity<String[]> response = restTemplate.exchange(END_POINT + showId + "/availability", HttpMethod.GET,
				null, String[].class);
		try {
			for (String str : response.getBody()) {
				System.out.println(str);
			}
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
		}
	}

	/**
	 * Book <Show Number> <Phone#> <Comma separated list of seats> (This must
	 * generate a unique ticket # and display)
	 * 
	 * @param showId
	 * @param phoneNum
	 * @param csSeats
	 * @throws AdminException
	 */
	@Override
	public void bookTicket(long showId, String phoneNum, String csSeats) throws AdminException {
		logger.debug("Book Ticket");

		HttpEntity<String> request = new HttpEntity<String>(prepareBookingDto(showId, phoneNum, csSeats), headers);
		long ticketId = restTemplate.postForObject(END_POINT + showId + "/book", request, Long.class);
		if (ticketId < 0) {
			System.out.printf("Booking failed for Show [%d], tickets [%s]", showId, csSeats);
			return;
		}
		System.out.printf("Booking succeeded, your ticket Id is %d %n", ticketId);
	}

	private String prepareBookingDto(long showId, String phoneNum, String csSeats) {
		JSONObject obj = new JSONObject().appendField("id", showId).appendField("phoneNum", phoneNum)
				.appendField("csSeats", csSeats);
		logger.debug(obj.toJSONString());
		return obj.toString();
	}

	/**
	 * Cancel <Show Number> <Phone#> <Ticket#>
	 * 
	 * @param showId
	 * @param phoneNum
	 * @param ticketNum
	 * @throws AdminException
	 */
	@Override
	public void cancelTicket(long showId, String phoneNum, long ticketNum) throws AdminException {
		logger.debug("Cancel Ticket");

		restTemplate.exchange(END_POINT + "{showId}/cancel?ticketNum={ticketNum},phoneNum={phoneNum}",
				HttpMethod.DELETE, null, String.class, showId, phoneNum, ticketNum);
	}

	@Override
	public void process(Object[] args) throws Exception {
		super.process(args);
	}

	@Override
	public void setupShow(long showId, int numOfRows, int numOfSeatsPerRow, int cancelWindow) throws BuyerException {
		throw new BuyerException();
	}

	@Override
	public void viewShow(long showId) throws BuyerException {
		throw new BuyerException();
	}

	@Override
	public void removeSeatsFromShow(long showId, int numOfSeats) throws BuyerException {
		throw new BuyerException();
	}

	@Override
	public void addSeatsToShow(long showId, int numOfRows) throws BuyerException {
		throw new BuyerException();
	}

}
