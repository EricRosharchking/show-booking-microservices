package com.liyuan.hong.showbooking.controller;

import com.liyuan.hong.showbooking.domain.Operation;
import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

public abstract class AppController {

	public void process(Object[] args) throws Exception {
		Operation op = (Operation) args[0];
		switch (op) {
		case SETUP:
			setupShow((int)args[1], Integer.valueOf((String) args[2]), Integer.valueOf((String) args[3], Integer.valueOf((String) args[4])), 0);
			break;
		case VIEW:
			viewShow((int)args[1]);
			break;
		case REMOVE:
			removeSeatsFromShow((int)args[1], Integer.valueOf((String) args[2]));
			break;
		case ADD:
			addSeatsToShow((int)args[1], Integer.valueOf((String) args[2]));
			break;
		case AVAILABILITY:
			checkShowAvailability((int)args[1]);
			break;
		case BOOK:
			bookTicket((int)args[1], (String)args[2], (String) args[3]);
			break;
		case CANCEL:
			cancelTicket((int)args[1], (String) args[2], Integer.valueOf((String) args[3]));
			break;
		default:
			throw new Exception();
		}
	}

	public abstract void setupShow(int showId, int numOfRows, int numOfSeatsPerRow, int cancelWindow) throws BuyerException;

	public abstract void viewShow(int showId) throws BuyerException;

	public abstract void removeSeatsFromShow(int showId, int numOfSeats) throws BuyerException;

	public abstract void addSeatsToShow(int showId, int numOfRows) throws BuyerException;

	public abstract void checkShowAvailability(int showId) throws AdminException;

	public abstract void bookTicket(int showId, String phoneNum, String csSeats) throws AdminException;

	public abstract void cancelTicket(int showId, String phoneNum, int ticketNum) throws AdminException;
}
