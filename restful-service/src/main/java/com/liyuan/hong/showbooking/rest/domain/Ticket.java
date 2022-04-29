package com.liyuan.hong.showbooking.rest.domain;

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
	private BookedRow bookedRow;


	public Show getShow() {
		return show;
	}

	public void setShow(Show show) {
		this.show = show;
	}

}
