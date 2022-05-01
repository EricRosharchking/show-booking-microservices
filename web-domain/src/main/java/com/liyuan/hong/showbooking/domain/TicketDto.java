package com.liyuan.hong.showbooking.domain;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TicketDto {

	@NotNull
	private long id;

	@NotNull
	private long showId;

	@NotNull
	@NotEmpty
	private Set<String> bookedSeats;

	public TicketDto(long id, long showId, Set<String> bookedSeats) {
		super();
		this.id = id;
		this.showId = showId;
		this.bookedSeats = bookedSeats;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getShowId() {
		return showId;
	}

	public void setShowId(long showId) {
		this.showId = showId;
	}

	public Set<String> getBookedSeats() {
		return bookedSeats;
	}

	public void setBookedSeats(Set<String> bookedSeats) {
		this.bookedSeats = bookedSeats;
	}

	@Override
	public String toString() {
		return "TicketDto [id=" + id + ", showId=" + showId + ", bookedSeats=" + bookedSeats + "]";
	}


}
