package com.liyuan.hong.showbooking.rest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
public class AvailableRow {

	@Id
	@GeneratedValue
	private long rowId;

	@ManyToOne
	private Show show;
	
	@Column
	private Character rowChar;

	@Column
	private int seats;

	public AvailableRow() {
		super();
	}

	public AvailableRow(Show show, char rowChar) {
		setShow(show);
		setRowChar(rowChar);
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

	public char getRowChar() {
		return rowChar;
	}

	public void setRowChar(char rowChar) {
		this.rowChar = rowChar;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	@Override
	public String toString() {
		return "AvailableRow [rowId=" + rowId + ", show=" + show + ", rowChar=" + rowChar + ", seats=" + seats + "]";
	}
}
