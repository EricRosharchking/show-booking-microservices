package com.liyuan.hong.showbooking.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

import net.minidev.json.JSONObject;

public class AppBuyerController extends AppController {

	private final String SHOW_END_POINT = "http://localhost:8080/show/";
	private final String TICKET_END_POINT = "http://localhost:8080/ticket/";
	private final String SEND_REQ = "Sending Request to [%s]%n";
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

	@Override
	public void checkShowAvailability(long showNum) throws AdminException {
		logger.printf(Level.DEBUG, SEND_REQ,"Check Show Availability");
		try {
			ResponseEntity<String[]> response = restTemplate.exchange(SHOW_END_POINT + showNum + "/availability",
					HttpMethod.GET, null, String[].class);
			logger.debug(response.getHeaders().toString());
			if (response.getStatusCode() == HttpStatus.OK) {
				for (String str : response.getBody()) {
					System.out.println(str);
				}
				logger.debug("Check Show Availability succeeded");
				return;
			} else {
				logger.info("Check Show Availability failed");
			}
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
		}
	}

	/**
	 * Book <Show Number> <Phone#> <Comma separated list of seats> (This must
	 * generate a unique ticket # and display)
	 * 
	 * @param showNum
	 * @param phoneNum
	 * @param csSeats
	 * @throws AdminException
	 */
	@Override
	public void bookTicket(long showNum, String phoneNum, String csSeats) throws AdminException {
		logger.printf(Level.DEBUG, SEND_REQ,"Book Ticket");
		HttpEntity<String> request = new HttpEntity<String>(prepareBookingDto(showNum, phoneNum, csSeats), headers);
		try {
			ResponseEntity<Long> response = restTemplate.postForEntity(TICKET_END_POINT + showNum + "/book", request,
					Long.class);
			System.out.printf("Booking succeeded, your ticket Id is %d %n", response.getBody());
		} catch (HttpStatusCodeException e) {
			System.out.printf("Booking ticket to Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding or having error, " + e.getMessage());
		}
	}

	private String prepareBookingDto(long showNum, String phoneNum, String csSeats) {
		JSONObject obj = new JSONObject().appendField("showNum", showNum).appendField("phoneNum", phoneNum)
				.appendField("csSeats", csSeats);
		logger.debug(obj.toJSONString());
		return obj.toString();
	}

	/**
	 * Cancel <Show Number> <Phone#> <Ticket#>
	 * 
	 * @param showNum
	 * @param phoneNum
	 * @param ticketNum
	 * @throws AdminException
	 */
	@Override
	public void cancelTicket(long showNum, String phoneNum, long ticketNum) throws AdminException {
		logger.printf(Level.DEBUG, SEND_REQ, "Cancel Ticket");

		try {
			ResponseEntity<Boolean> response = restTemplate.exchange(TICKET_END_POINT + "{showNum}/cancel?ticketNum={ticketNum}&phoneNum={phoneNum}",
					HttpMethod.DELETE, null, Boolean.class, showNum, phoneNum, ticketNum);
			System.out.printf("Cancel Ticket request %s%n", response.getBody() ? "succeeded" : "failed");
		} catch (HttpStatusCodeException e) {
			logger.error(e.getMessage());
			System.out.printf("Booking ticket to Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding or having error, " + e.getMessage());
		}
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
