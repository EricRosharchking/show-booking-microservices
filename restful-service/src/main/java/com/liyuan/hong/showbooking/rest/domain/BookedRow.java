package com.liyuan.hong.showbooking.rest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BookedRow {

	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	private ShowRow row;

	@Column
	private int seats;

	public BookedRow() {
		super();
	}

	public BookedRow(ShowRow row, int seats) {
		this.row = row;
		this.seats = seats;
	}
	
	public long getRowId() {
		return id;
	}

	public void setRowId(long id) {
		this.id = id;
	}

	public ShowRow getRow() {
		return row;
	}

	public void setRow(ShowRow row) {
		this.row = row;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

}
