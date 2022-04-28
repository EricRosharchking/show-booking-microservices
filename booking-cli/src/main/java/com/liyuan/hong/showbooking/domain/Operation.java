package com.liyuan.hong.showbooking.domain;

public enum Operation {

	SETUP("setup"),VIEW("view"),REMOVE("remove"),ADD("add"),AVAILABILITY("availability"),BOOK("book"),CANCEL("cancel");
	String op;
	private Operation(String op) {
		this.op = op;
	}
}
