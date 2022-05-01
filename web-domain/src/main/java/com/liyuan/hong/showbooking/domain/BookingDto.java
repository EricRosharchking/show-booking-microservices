package com.liyuan.hong.showbooking.domain;

public class BookingDto {

	private long showId;
	private String phoneNum;
	private String seats;

	public BookingDto(long showId, String phoneNum, String seats) {
		super();
		this.showId = showId;
		this.phoneNum = phoneNum;
		this.seats = seats;
	}

	public long getShowId() {
		return showId;
	}

	public void setShowId(long showId) {
		this.showId = showId;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getSeats() {
		return seats;
	}

	public void setSeats(String seats) {
		this.seats = seats;
	}

}
