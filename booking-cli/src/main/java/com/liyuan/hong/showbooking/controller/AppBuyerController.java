package com.liyuan.hong.showbooking.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
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

	private final String SHOW_END_POINT;
	private final String TICKET_END_POINT;
	private final String SEND_REQ = "Sending Request to [%s]%n";
	Logger logger = LogManager.getLogger(this.getClass());

	private RestTemplate restTemplate;
	private HttpHeaders headers;

	@Autowired
	public AppBuyerController(RestTemplateBuilder builder, Environment env) {
		this.restTemplate = builder.build();
		logger.debug("RestTemplate Built");
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		logger.debug("HttpHeaders set");
		SHOW_END_POINT = "http://" + env.getProperty("rest.end.point.server", "127.0.0.0") + ":"
				+ env.getProperty("rest.end.point.port", "8081") + "/show/";
		TICKET_END_POINT = "http://" + env.getProperty("rest.end.point.server", "127.0.0.0") + ":"
				+ env.getProperty("rest.end.point.port", "8081") + "/ticket/";
	}

	@Override
	public void checkShowAvailability(long showNum) throws AdminException {
		logger.printf(Level.DEBUG, SEND_REQ, "Check Show Availability");
		try {
			ResponseEntity<String[]> response = restTemplate.exchange(SHOW_END_POINT + showNum + "/availability",
					HttpMethod.GET, null, String[].class);
			logger.trace(response.getHeaders().toString());
			if (response.getStatusCode() == HttpStatus.OK) {
				for (String str : response.getBody()) {
					System.out.println(str);
				}
			} else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
				System.out.println("The show you are checking is not availble for booking");
			}
			logger.debug("Check Show Availability succeeded");
		} catch (HttpStatusCodeException e) {
			System.out.printf("Booking ticket to Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding or having error, " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void bookTicket(long showNum, String phoneNum, String csSeats) throws AdminException {
		logger.printf(Level.DEBUG, SEND_REQ, "Book Ticket");
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String prepareBookingDto(long showNum, String phoneNum, String csSeats) {
		JSONObject obj = new JSONObject().appendField("showNum", showNum).appendField("phoneNum", phoneNum)
				.appendField("csSeats", csSeats);
		logger.debug(obj.toJSONString());
		return obj.toString();
	}

	@Override
	public void cancelTicket(long showNum, String phoneNum, long ticketNum) throws AdminException {
		logger.printf(Level.DEBUG, SEND_REQ, "Cancel Ticket");
		try {
			ResponseEntity<Boolean> response = restTemplate.exchange(
					TICKET_END_POINT + "{showNum}/cancel?ticketNum={ticketNum}&phoneNum={phoneNum}", HttpMethod.DELETE,
					null, Boolean.class, showNum, phoneNum, ticketNum);
			System.out.printf("Cancel Ticket request %s%n", response.getBody() ? "succeeded" : "failed");
		} catch (HttpStatusCodeException e) {
			logger.error(e.getMessage());
			System.out.printf("Booking ticket to Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding or having error, " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
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
