package com.liyuan.hong.showbooking.controller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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

	private final String SHOW_END_POINT;
	private final String TICKET_END_POINT;
	private final String SEND_REQ = "Sending Request to [%s]%n";

	Logger logger = LogManager.getLogger(this.getClass());

	private RestTemplate restTemplate;
	private HttpHeaders headers;

	@Autowired
	public AppAdminController(RestTemplateBuilder builder, Environment env) {
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
	public void setupShow(long showNum, int numOfRows, int numOfSeatsPerRow, int cancelWindow) throws BuyerException {
		logger.printf(Level.DEBUG, SEND_REQ, "Setup Show");
		try {
			ResponseEntity<Boolean> response = restTemplate.postForEntity(SHOW_END_POINT + showNum + "/setup",
					prepareShowDto(showNum, numOfRows, numOfSeatsPerRow, cancelWindow), Boolean.class);
			logger.printf(Level.INFO, "Setup show %s%n", response.getBody() ? "succeeded" : "failed");
			System.out.printf("Setup show %s%n", response.getBody() ? "succeeded" : "failed");
		} catch (HttpStatusCodeException e) {
			System.out.printf("Setting up Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding or having error, " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JSONObject prepareShowDto(long showNum, int numOfRows, int numOfSeatsPerRow, int cancelWindow) {
		JSONObject obj = new JSONObject().appendField("id", showNum).appendField("rows", numOfRows)
				.appendField("seats", numOfSeatsPerRow).appendField("cancelWindow", cancelWindow);
		logger.debug(obj.toJSONString());
		return obj;
	}

	@Override
	public void viewShow(long showNum) throws BuyerException {
		logger.printf(Level.DEBUG, SEND_REQ, "View Show");
		try {
			ResponseEntity<TicketDto[]> response = restTemplate.getForEntity(TICKET_END_POINT + showNum + "/view",
					TicketDto[].class);
			if (response.getStatusCode() == HttpStatus.OK) {
				for (TicketDto ticket : response.getBody()) {
					System.out.printf("View Show %s%n", ticket.toString());
				}
			} else {
				System.out.println("The Show has not been booked");
			}
			logger.info("View Show succeeded");
		} catch (HttpStatusCodeException e) {
			System.out.printf("Setting up Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding or having error, " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeSeatsFromShow(long showNum, int numOfSeats) throws BuyerException {
		logger.printf(Level.DEBUG, SEND_REQ, "Remove seats from Show");
		String result = "failed";
		try {
			result = restTemplate.postForObject(SHOW_END_POINT + showNum + "/removeSeats?seats={numOfSeats}", null,
					String.class, numOfSeats);
			System.out.printf("Removing [%d] seats from show [%d] %s%n", numOfSeats, showNum, result);
		} catch (HttpStatusCodeException e) {
			System.out.printf("Removing seats from Show failed, server returned error is [%s]%n",
					e.getResponseHeaders().getFirst("reasonOfFailure"));
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding or having error, " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addSeatsToShow(long showNum, int numOfRows) throws BuyerException {
		logger.printf(Level.DEBUG, SEND_REQ, "Add rows to Show");
		try {
			boolean status = restTemplate.postForObject(SHOW_END_POINT + showNum + "/addRows?rows={numOfRows}", null,
					Boolean.class, numOfRows);
			logger.printf(Level.INFO, "Added %d rows to Show %d %s.%n", numOfRows, showNum,
					status ? "succeeded" : "failed");
		} catch (HttpStatusCodeException e) {
			System.out.printf("Adding seats to Show failed, server returned error is [%s]%n",
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
