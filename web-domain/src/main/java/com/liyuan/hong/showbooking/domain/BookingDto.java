package com.liyuan.hong.showbooking.domain;

public class BookingDto {

	private long showId;
	private String phoneNum;
	private String csSeats;

	public BookingDto(long showId, String phoneNum, String seats) {
		super();
		this.showId = showId;
		this.phoneNum = phoneNum;
		this.csSeats = seats;
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

	public String getCsSeats() {
		return csSeats;
	}

	public void setCsSeats(String seats) {
		this.csSeats = seats;
	}

}
