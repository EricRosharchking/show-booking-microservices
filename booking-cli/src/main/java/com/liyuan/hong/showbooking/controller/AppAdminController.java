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
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.liyuan.hong.showbooking.domain.TicketDto;
import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

import net.minidev.json.JSONObject;

@Controller
public class AppAdminController extends AppController {

	private final String END_POINT = "http://localhost:8080/show/";

	Logger logger = LogManager.getLogger(AppAdminController.class);

	private RestTemplate restTemplate;
	private HttpHeaders headers;

	@Autowired
	public AppAdminController(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
		logger.debug("RestTemplate Built");
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		logger.debug("HttpHeaders set");
	}

	/**
	 * 1.Setup <Show Number> <Number of Rows> <Number of seats per row>
	 * <Cancellation window in minutes>
	 * 
	 * @param showId
	 * @param numOfRows
	 * @param numOfSeatsPerRow
	 * @throws BuyerException
	 */
	@Override
	public void setupShow(long showId, int numOfRows, int numOfSeatsPerRow, int cancelWindow) throws BuyerException {
		logger.debug("Setup Show");
		String output = "failed";
		try {
			ResponseEntity<Boolean> response = restTemplate.postForEntity(END_POINT + showId + "/setup",
					prepareShowDto(showId, numOfRows, numOfSeatsPerRow, cancelWindow), Boolean.class);
			output = response.getBody() ? "succeeded" : output;
		} catch (HttpStatusCodeException e) {
			System.out.printf("Setting up Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding or is having error");
			return;
		}
		logger.printf(Level.INFO, "Setup show %s%n", output);
	}

	private JSONObject prepareShowDto(long showId, int numOfRows, int numOfSeatsPerRow, int cancelWindow) {
		JSONObject obj = new JSONObject().appendField("id", showId).appendField("rows", numOfRows)
				.appendField("seats", numOfSeatsPerRow).appendField("cancelWindow", cancelWindow);
		logger.debug(obj.toJSONString());
		return obj;
	}

	/**
	 * 2.View <Show Number> (Display Show Number, Ticket#, Buyer Phone#, Seat
	 * Numbers allocated to the buyer)
	 * 
	 * @param showId
	 * @throws BuyerException
	 */
	@Override
	public void viewShow(long showId) throws BuyerException {
		logger.debug("View Show");
		try {
			ResponseEntity<TicketDto[]> response = restTemplate.getForEntity(END_POINT + showId + "/view",
					TicketDto[].class);
			if (response.getStatusCode() == HttpStatus.OK) {
				if (response.getBody().length == 0) {
					logger.info("The Show has not been booked");
				}
				for (TicketDto ticket : response.getBody()) {
					logger.printf(Level.INFO, "View Show %s%n", ticket.toString());
				}
			}
		} catch (HttpStatusCodeException e) {
			System.out.printf("Setting up Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
			return;
		}
		logger.info("View Show succeeded");

	}

	/**
	 * 3.Remove <Show Number> <count of seats to be reduced>
	 * 
	 * @param showId
	 * @param numOfSeats
	 * @throws BuyerException
	 */
	@Override
	public void removeSeatsFromShow(long showId, int numOfSeats) throws BuyerException {
		logger.debug("Remove seats from Show");
		String result = "failed";
		try {
			result = restTemplate.postForObject(END_POINT + showId + "/removeSeats?seats={numOfSeats}", null,
					String.class, numOfSeats);
		} catch (HttpStatusCodeException e) {
			System.out.printf("Removing seats from Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
			return;
		}
		logger.printf(Level.INFO, "%s.%n", numOfSeats, showId, result);
	}

	/**
	 * 4.Add <Show Number> <number of rows to be added>
	 * 
	 * @param showId
	 * @param numOfRows
	 * @throws BuyerException
	 */
	@Override
	public void addSeatsToShow(long showId, int numOfRows) throws BuyerException {
		logger.debug("Add rows to Show");
		String result = "failed";
		try {
			boolean status = restTemplate.postForObject(END_POINT + showId + "/addRows?rows={numOfRows}", null,
					Boolean.class, numOfRows);
			result = status ? "succeeded" : result;
		} catch (HttpStatusCodeException e) {
			System.out.printf("Adding seats to Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
			return;
		}
		logger.printf(Level.INFO, "Adding %d rows to Show %d %s.%n", numOfRows, showId, result);
	}

	@Override
	public void process(Object[] args) throws Exception {
		super.process(args);
	}

	@Override
	public void checkShowAvailability(long showId) throws AdminException {
		logger.debug("Check Show Availability");
		try {
			ResponseEntity<String[]> response = restTemplate.exchange(END_POINT + showId + "/availability",
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

//	@Override
//	public void checkShowAvailability(long showId) throws AdminException {
//		throw new AdminException();
//	}
//
//	@Override
//	public void bookTicket(long showId, String phoneNum, String csSeats) throws AdminException {
//		throw new AdminException();
//	}
//
//	@Override
//	public void cancelTicket(long showId, String phoneNum, long ticketId) throws AdminException {
//		throw new AdminException();
//	}

}
