package com.liyuan.hong.showbooking.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
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
	private final String SEND_REQ = "Sending Request to [%s]%n";

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
	 * @param showNum
	 * @param numOfRows
	 * @param numOfSeatsPerRow
	 * @throws BuyerException
	 */
	@Override
	public void setupShow(long showNum, int numOfRows, int numOfSeatsPerRow, int cancelWindow) throws BuyerException {
		logger.printf(Level.DEBUG, SEND_REQ,"Setup Show");
		String output = "failed";
		try {
			ResponseEntity<Boolean> response = restTemplate.postForEntity(END_POINT + showNum + "/setup",
					prepareShowDto(showNum, numOfRows, numOfSeatsPerRow, cancelWindow), Boolean.class);
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

	private JSONObject prepareShowDto(long showNum, int numOfRows, int numOfSeatsPerRow, int cancelWindow) {
		JSONObject obj = new JSONObject().appendField("id", showNum).appendField("rows", numOfRows)
				.appendField("seats", numOfSeatsPerRow).appendField("cancelWindow", cancelWindow);
		logger.debug(obj.toJSONString());
		return obj;
	}

	/**
	 * 2.View <Show Number> (Display Show Number, Ticket#, Buyer Phone#, Seat
	 * Numbers allocated to the buyer)
	 * 
	 * @param showNum
	 * @throws BuyerException
	 */
	@Override
	public void viewShow(long showNum) throws BuyerException {
		logger.printf(Level.DEBUG, SEND_REQ, "View Show");
		try {
			ResponseEntity<TicketDto[]> response = restTemplate.getForEntity(END_POINT + showNum + "/view",
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
	 * @param showNum
	 * @param numOfSeats
	 * @throws BuyerException
	 */
	@Override
	public void removeSeatsFromShow(long showNum, int numOfSeats) throws BuyerException {
		logger.printf(Level.DEBUG, SEND_REQ,"Remove seats from Show");
		String result = "failed";
		try {
			result = restTemplate.postForObject(END_POINT + showNum + "/removeSeats?seats={numOfSeats}", null,
					String.class, numOfSeats);
		} catch (HttpStatusCodeException e) {
			System.out.printf("Removing seats from Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
			return;
		}
		logger.printf(Level.INFO, "%s.%n", numOfSeats, showNum, result);
	}

	/**
	 * 4.Add <Show Number> <number of rows to be added>
	 * 
	 * @param showNum
	 * @param numOfRows
	 * @throws BuyerException
	 */
	@Override
	public void addSeatsToShow(long showNum, int numOfRows) throws BuyerException {
		logger.printf(Level.DEBUG, SEND_REQ,"Add rows to Show");
		String result = "failed";
		try {
			boolean status = restTemplate.postForObject(END_POINT + showNum + "/addRows?rows={numOfRows}", null,
					Boolean.class, numOfRows);
			result = status ? "succeeded" : result;
		} catch (HttpStatusCodeException e) {
			System.out.printf("Adding seats to Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
			return;
		}
		logger.printf(Level.INFO, "Added %d rows to Show %d %s.%n", numOfRows, showNum, result);
	}

	@Override
	public void process(Object[] args) throws Exception {
		super.process(args);
	}

	@Override
	public void checkShowAvailability(long showNum) throws AdminException {
		throw new AdminException();
	}

	@Override
	public void bookTicket(long showNum, String phoneNum, String csSeats) throws AdminException {
		throw new AdminException();
	}

	@Override
	public void cancelTicket(long showNum, String phoneNum, long ticketId) throws AdminException {
		throw new AdminException();
	}

}
