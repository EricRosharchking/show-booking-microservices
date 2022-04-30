package com.liyuan.hong.showbooking.controller;

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.liyuan.hong.showbooking.domain.Operation;
import com.liyuan.hong.showbooking.domain.ShowDto;
import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

@Controller
public class AppAdminController extends AppController {
	Logger logger = LogManager.getLogger(AppAdminController.class);

	RestTemplate restTemplate;

	public AppAdminController(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
	}

	@Override
	public void process(Object[] args) throws Exception {
		super.process(args);
		logger.debug(args);
		Operation op = (Operation) args[0];
		switch (op) {
		case SETUP:
			setupShow((int) args[1], Integer.valueOf((String) args[2]), Integer.valueOf((String) args[3]),
					Integer.valueOf((String) args[4]));
			break;
		case VIEW:
			viewShow((int) args[1]);
			break;
		case REMOVE:
			removeSeatsFromShow((int) args[1], Integer.valueOf((String) args[2]));
			break;
		case ADD:
			addSeatsToShow((int) args[1], Integer.valueOf((String) args[2]));
			break;
		case AVAILABILITY:
			checkShowAvailability((int) args[1]);
			break;
		case BOOK:
			bookTicket((int) args[1], (String) args[2], (String) args[3]);
			break;
		case CANCEL:
			cancelTicket((int) args[1], (String) args[2], Integer.valueOf((String) args[3]));
			break;
		default:
			throw new Exception();
		}
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
	public void setupShow(int showId, int numOfRows, int numOfSeatsPerRow, int cancelWindow) throws BuyerException {
		logger.debug("Setup Show");
		logger.debug("RestTemplate Built %n");
		String output = "failed";
		try {
			boolean result = restTemplate.postForObject("http://localhost:8080/shows/setup",
					new ShowDto(showId, numOfRows, numOfSeatsPerRow, cancelWindow), Boolean.class);
			output = result ? "succeeded" : output;
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
		}
		logger.printf(Level.INFO, "Setup show %s%n", output);
	}

	/**
	 * 2.View <Show Number> (Display Show Number, Ticket#, Buyer Phone#, Seat
	 * Numbers allocated to the buyer)
	 * 
	 * @param showId
	 * @throws BuyerException
	 */
	@Override
	public void viewShow(int showId) throws BuyerException {
		logger.debug("View Show");
		try {
			ShowDto showDto = restTemplate.getForObject(null, ShowDto.class);
			logger.printf(Level.INFO, "View Show %s%n", showDto.toString());
		} catch (RestClientException e) {
			logger.info("Rest Server is not Responding");
		}

	}

	/**
	 * 3.Remove <Show Number> <count of seats to be reduced>
	 * 
	 * @param showId
	 * @param numOfSeats
	 * @throws BuyerException
	 */
	@Override
	public void removeSeatsFromShow(int showId, int numOfSeats) throws BuyerException {
		// TODO Auto-generated method stub

	}

	/**
	 * 4.Add <Show Number> <number of rows to be added>
	 * 
	 * @param showId
	 * @param numOfRows
	 * @throws BuyerException
	 */
	@Override
	public void addSeatsToShow(int showId, int numOfRows) throws BuyerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkShowAvailability(int showId) throws AdminException {
		throw new AdminException();
	}

	@Override
	public void bookTicket(int showId, String phoneNum, String csSeats) throws AdminException {
		throw new AdminException();
	}

	@Override
	public void cancelTicket(int showId, String phoneNum, int ticketNum) throws AdminException {
		throw new AdminException();
	}

}
