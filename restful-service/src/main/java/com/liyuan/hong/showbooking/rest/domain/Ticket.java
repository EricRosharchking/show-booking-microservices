package com.liyuan.hong.showbooking.rest.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "show_id", "phone_num" }) })
public class Ticket {

	@Id
	@GeneratedValue
	@Column
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "show_id")
	private Show show;

	@Column(name="phone_num")
	private String phoneNum;

	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime bookedTime;

	@Column
	private int numOfSeats;

	@OneToMany
	private Set<BookedRow> bookedRow;

	public Ticket() {
		super();
	}

	public Ticket(Show show, String phoneNum, LocalDateTime bookedTime, int numOfSeats, Set<BookedRow> bookedRow) {
		super();
		this.show = show;
		this.phoneNum = phoneNum;
		this.bookedTime = bookedTime;
		this.numOfSeats = numOfSeats;
		this.bookedRow = bookedRow;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Show getShow() {
		return show;
	}

	public void setShow(Show show) {
		this.show = show;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public LocalDateTime getBookedTime() {
		return bookedTime;
	}

	public void setBookedTime(LocalDateTime bookedTime) {
		this.bookedTime = bookedTime;
	}
	
	public int getNumOfSeats() {
		return numOfSeats;
	}

	public void setNumOfSeats(int numOfSeats) {
		this.numOfSeats = numOfSeats;
	}

	public Set<BookedRow> getBookedRow() {
		return bookedRow;
	}

	public void setBookedRow(Set<BookedRow> bookedRow) {
		this.bookedRow = bookedRow;
	}

	public Set<String> getBookedSeats() {
		return Set.of(bookedRow.stream().map(r -> String.valueOf(r.getSeats())).toArray(String[]::new));
	}

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", show=" + show.getId() + ", phoneNum=" + phoneNum + ", bookedTime="
				+ bookedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ", bookedRow=" + bookedRow
				+ "]";
	}

}
