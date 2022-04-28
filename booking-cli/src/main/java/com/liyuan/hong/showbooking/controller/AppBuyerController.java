package com.liyuan.hong.showbooking.controller;

import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

public class AppBuyerController extends AppController {

	@Override
	public void process(Object[] args) throws Exception {
		super.process(args);
	}

	@Override
	public void setupShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException {
		throw new BuyerException();
	}

	@Override
	public void viewShow(int showId) throws BuyerException {
		throw new BuyerException();
	}

	@Override
	public void removeSeatsFromShow(int showId, int numOfSeats) throws BuyerException {
		throw new BuyerException();
	}

	@Override
	public void addSeatsToShow(int showId, int numOfRows) throws BuyerException {
		throw new BuyerException();
	}

	/**
	 * Availability <Show Number> (List all available seat numbers for a show. E.g
	 * A1, F4 etc)
	 * 
	 * @param showId
	 * @throws AdminException
	 */
	@Override
	public void checkShowAvailability(int showId) throws AdminException {
		// TODO Auto-generated method stub

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
	public void bookTicket(int showId, String phoneNum, String csSeats) throws AdminException {
		// TODO Auto-generated method stub

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
	public void cancelTicket(int showId, String phoneNum, int ticketNum) throws AdminException {
		// TODO Auto-generated method stub

	}

}
