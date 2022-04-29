package com.liyuan.hong.showbooking.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ShowDto {

	@NotNull
	private long id;

	@Min(1)
	@Max(26)
	private int rows;
	@Min(1)
	@Max(10)
	private int seats;

	@Min(0)
	@Max(60)
	private int cancelWindow;

	public ShowDto(long id, int rows, int seats, int cancelWindow) {
		super();
		this.id = id;
		this.rows = rows;
		this.seats = seats;
		this.cancelWindow = cancelWindow;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public int getCancelWindow() {
		return cancelWindow;
	}

	public void setCancelWindow(int cancelWindow) {
		this.cancelWindow = cancelWindow;
	}

}
