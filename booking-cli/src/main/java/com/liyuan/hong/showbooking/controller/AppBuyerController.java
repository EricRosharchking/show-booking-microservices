package com.liyuan.hong.showbooking.controller;

import com.liyuan.hong.showbooking.exception.BuyerException;

public class AppBuyerController extends AppController{

	@Override
	public void process(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException {
		throw new BuyerException();
	}

	@Override
	public void viewShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException {
		throw new BuyerException();
		
	}

	@Override
	public void removeSeatsFromShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException {
		throw new BuyerException();
		
	}

	@Override
	public void addSeatsToShow(int showId, int numOfRows, int numOfSeatsPerRow) throws BuyerException {
		throw new BuyerException();
		
	}

	@Override
	public void checkShowAvailability(int showId, int numOfRows, int numOfSeatsPerRow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookTicket(int showId, int numOfRows, int numOfSeatsPerRow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelTicket(int showId, int numOfRows, int numOfSeatsPerRow) {
		// TODO Auto-generated method stub
		
	}

}
