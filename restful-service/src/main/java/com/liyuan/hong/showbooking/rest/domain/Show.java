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
	private int numOfRows;

	@Column
	private int seatsPerRow;
	
	@Column
	private int availableSeats;

	@Column
	private int cancelWindow;
	
	public Show() {
		super();
	}

	public Show(long id, int numOfRows, int seatsPerRow, int availableSeats, int cancelWindow) {
		super();
		this.id = id;
		this.numOfRows = numOfRows;
		this.seatsPerRow = seatsPerRow;
		this.availableSeats = availableSeats;
		this.cancelWindow = cancelWindow;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getNumOfRows() {
		return numOfRows;
	}

	public void setNumOfRows(int numOfRows) {
		this.numOfRows = numOfRows;
	}

	public int getSeatsPerRow() {
		return seatsPerRow;
	}

	public void setSeatsPerRow(int seatsPerRow) {
		this.seatsPerRow = seatsPerRow;
	}

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int seatsPerRow) {
		this.availableSeats = seatsPerRow;
	}

	public int getCancelWindow() {
		return cancelWindow;
	}

	public void setCancelWindow(int cancelWindow) {
		this.cancelWindow = cancelWindow;
	}

}
