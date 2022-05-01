package com.liyuan.hong.showbooking.rest.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
public class Ticket {

	@Id
	@GeneratedValue
	@Column
	private long id;

	@ManyToOne
	private Show show;
	
	@OneToMany
	private Set<BookedRow> bookedRow;


	public Ticket() {
		super();
	}

	public Show getShow() {
		return show;
	}

	public void setShow(Show show) {
		this.show = show;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Set<BookedRow> getBookedRow() {
		return bookedRow;
	}

	public void setBookedRow(Set<BookedRow> bookedRow) {
		this.bookedRow = bookedRow;
	}

	public Set<String> getBookedSeats() {
		return Set.of(bookedRow.stream().map(r -> r.getSeats()).toArray(String[]::new));
	}

}
