package com.liyuan.hong.showbooking.controller;

import com.liyuan.hong.showbooking.domain.Operation;
import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

public abstract class AppController {

	public void process(Object[] args) throws Exception {
		System.out.println("Processing Request");
		
	}

	public abstract void setupShow(int showId, int numOfRows, int numOfSeatsPerRow, int cancelWindow) throws BuyerException;

	public abstract void viewShow(int showId) throws BuyerException;

	public abstract void removeSeatsFromShow(int showId, int numOfSeats) throws BuyerException;

	public abstract void addSeatsToShow(int showId, int numOfRows) throws BuyerException;

	public abstract void checkShowAvailability(int showId) throws AdminException;

	public abstract void bookTicket(int showId, String phoneNum, String csSeats) throws AdminException;

	public abstract void cancelTicket(int showId, String phoneNum, int ticketNum) throws AdminException;
}
