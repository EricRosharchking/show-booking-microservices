package com.liyuan.hong.showbooking.controller;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.liyuan.hong.showbooking.domain.Operation;
import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

public abstract class AppController {
	Logger logger = LogManager.getLogger(AppController.class);

	public void process(Object[] args) throws Exception {
		System.out.println("Processing Request");
		logger.debug(Arrays.toString(args));
		Operation op = (Operation) args[0];
		switch (op) {
		case SETUP:
			setupShow((long) args[1], Integer.valueOf((String) args[2]), Integer.valueOf((String) args[3]),
					Integer.valueOf((String) args[4]));
			break;
		case VIEW:
			viewShow((long) args[1]);
			break;
		case REMOVE:
			removeSeatsFromShow((long) args[1], Integer.valueOf((String) args[2]));
			break;
		case ADD:
			addSeatsToShow((long) args[1], Integer.valueOf((String) args[2]));
			break;
		case AVAILABILITY:
			checkShowAvailability((long) args[1]);
			break;
		case BOOK:
			bookTicket((long) args[1], (String) args[2], (String) args[3]);
			break;
		case CANCEL:
			cancelTicket((long) args[1], (String) args[2], Long.valueOf((String) args[3]));
			break;
		default:
			throw new Exception();
		}
	}

	public abstract void setupShow(long showNum, int numOfRows, int numOfSeatsPerRow, int cancelWindow) throws BuyerException;

	public abstract void viewShow(long showNum) throws BuyerException;

	public abstract void removeSeatsFromShow(long showNum, int numOfSeats) throws BuyerException;

	public abstract void addSeatsToShow(long showNum, int numOfRows) throws BuyerException;

	public abstract void checkShowAvailability(long showNum) throws AdminException;

	public abstract void bookTicket(long showNum, String phoneNum, String csSeats) throws AdminException;

	public abstract void cancelTicket(long showNum, String phoneNum, long ticketId) throws AdminException;
}
