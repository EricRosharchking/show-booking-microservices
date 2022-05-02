package com.liyuan.hong.showbooking.rest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Show {

	@Id
	@Column
	private long id;

	@Column
	private int rows;

	@Column
	private int seats;
	
	@Column
	private int availableSeats;
	
	@Column
	private int blockedSeats;

	@Column
	private int cancelWindow;
	
	public Show() {
		super();
	}

	public Show(long id, int rows, int seats, int availableSeats, int blockedSeats, int cancelWindow) {
		super();
		this.id = id;
		this.rows = rows;
		this.seats = seats;
		this.availableSeats = availableSeats;
		this.blockedSeats = blockedSeats;
		this.cancelWindow = cancelWindow;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int seats) {
		this.availableSeats = seats;
	}

	public int getBlockedSeats() {
		return blockedSeats;
	}

	public void setBlockedSeats(int seats) {
		this.blockedSeats = seats;
	}

	public int getCancelWindow() {
		return cancelWindow;
	}

	public void setCancelWindow(int cancelWindow) {
		this.cancelWindow = cancelWindow;
	}

}
