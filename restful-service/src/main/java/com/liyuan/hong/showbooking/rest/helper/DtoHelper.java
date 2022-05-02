package com.liyuan.hong.showbooking.rest.helper;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Controller;

import com.liyuan.hong.showbooking.domain.TicketDto;
import com.liyuan.hong.showbooking.rest.domain.BookedRow;
import com.liyuan.hong.showbooking.rest.domain.Ticket;

@Controller
public class DtoHelper {

	public DtoHelper() {
		
	}
	
	public TicketDto prepareTicketDtoFromTicket(Ticket t) {
		Set<String> csSeats = new HashSet<>();
		Set<BookedRow> rows = t.getBookedRow();
		for (BookedRow row: rows) {
			char rowChar = row.getRow().getRowChar();
			for (int i = 1; i <= 10; i++) {
				if (((1<<i) & row.getSeats()) != 0) {
					csSeats.add(rowChar+""+i);
				}
			}
		}
		return new TicketDto(t.getId(), t.getShow().getId(), csSeats);
	}
}
