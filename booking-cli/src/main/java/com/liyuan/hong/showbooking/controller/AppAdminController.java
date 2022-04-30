package com.liyuan.hong.showbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import com.liyuan.hong.showbooking.domain.ShowDto;
import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

@Controller
public class AppAdminController extends AppController {

	RestTemplate rest;

	@Autowired
	RestTemplateBuilder builder;

	@Override
	public void process(Object[] args) throws Exception {
		super.process(args);
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
		rest = builder.build();
		boolean result = rest.postForObject("http://localhost/shows/setup",
				new ShowDto(showId, numOfRows, numOfSeatsPerRow, cancelWindow), Boolean.class);
		String ouput = result ? "succeeded" : "failed";
		System.out.printf("Setup show %s", ouput);
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
		// TODO Auto-generated method stub

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
