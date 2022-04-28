package com.liyuan.hong.showbooking.controller;

import com.liyuan.hong.showbooking.exception.AdminException;
import com.liyuan.hong.showbooking.exception.BuyerException;

public abstract class AppController {

	public abstract void process(String[] args) throws Exception;
	public abstract void setupShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException;
	public abstract void viewShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException;
	public abstract void removeSeatsFromShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException;
	public abstract void addSeatsToShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException;
	public abstract void checkShowAvailability(int showId, int numOfRows, int numOfSeatsPerRow) throws AdminException;
	public abstract void bookTicket(int showId, int numOfRows, int numOfSeatsPerRow) throws AdminException;
	public abstract void cancelTicket(int showId, int numOfRows, int numOfSeatsPerRow) throws AdminException;
}
