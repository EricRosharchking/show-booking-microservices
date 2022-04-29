package com.liyuan.hong.showbooking.rest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
public class BlockedRow {

	@Id
	@GeneratedValue
	private long rowId;

	@ManyToOne
	private Show show;
	
	@Column
	private Character rowChar;

	@Column
	private int seats;

	public BlockedRow(Show show, char row) {
		setShow(show);
		setRow(row);
	}
	
	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public Show getShow() {
		return show;
	}

	public void setShow(Show show) {
		this.show = show;
	}

	public Character getRow() {
		return rowChar;
	}

	public void setRow(Character row) {
		this.rowChar = row;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

}
