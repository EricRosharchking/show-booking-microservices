package com.liyuan.hong.showbooking.rest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Show {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Column(name = "AVAILABLE_SEATS")
	private int availableSeats;

	@Column(name = "BLOCKED_SEATS")
	private int blockedSeats;

	@Column(name = "CANCEL_WINDOW")
	private int cancelWindow;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
	}

	public int getBlockedSeats() {
		return blockedSeats;
	}

	public void setBlockedSeats(int blockedSeats) {
		this.blockedSeats = blockedSeats;
	}

	public int getCancelWindow() {
		return cancelWindow;
	}

	public void setCancelWindow(int cancelWindow) {
		this.cancelWindow = cancelWindow;
	}

}
