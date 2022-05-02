package com.liyuan.hong.showbooking.rest.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.collections4.IterableUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.liyuan.hong.showbooking.rest.domain.AvailableRow;
import com.liyuan.hong.showbooking.rest.domain.BookedRow;
import com.liyuan.hong.showbooking.rest.domain.Show;
import com.liyuan.hong.showbooking.rest.domain.Ticket;
import com.liyuan.hong.showbooking.rest.repo.AvailableRowRepository;
import com.liyuan.hong.showbooking.rest.repo.BookedRowRepository;
import com.liyuan.hong.showbooking.rest.repo.ShowRepository;
import com.liyuan.hong.showbooking.rest.repo.TicketRepository;

@Service
public class TicketService {

	Logger logger = LogManager.getLogger(this.getClass());

	private AvailableRowRepository availableRowRepo;
	private BookedRowRepository bookedRowRepo;
	private ShowRepository showRepo;
	private TicketRepository ticketRepo;

	@Autowired
	public TicketService(AvailableRowRepository availableRowsRepo, BookedRowRepository bookedRowRepo,
			ShowRepository showRepo, TicketRepository ticketRepo) {
		super();
		this.availableRowRepo = availableRowsRepo;
		this.bookedRowRepo = bookedRowRepo;
		this.showRepo = showRepo;
		this.ticketRepo = ticketRepo;
	}

	public List<Ticket> viewShow(long showId) {
		return IterableUtils.toList(ticketRepo.findAllByShowId(showId));
	}

	public Ticket bookTicket(long showId, String phoneNum, String csSeats)
			throws IllegalStateException, DataIntegrityViolationException {
		Show show = showRepo.findById(showId)
				.orElseThrow(() -> new IllegalStateException("The Show with Id " + showId + " does not exist"));
		Map<Character, Integer> bookingSeatsInRows = getBookingSeatsInRows(csSeats);
		int numOfSeats = bookingSeatsInRows.get('.');
		bookingSeatsInRows.remove('.');
		if (show.getAvailableSeats() < numOfSeats) {
			throw new IllegalStateException(
					"There are not enough available seats to book, please check availability and try again");
		}
		Set<BookedRow> bookedRows = new HashSet<>();
//		prepareBookedRowsForShow(showId, bookingSeatsInRows);
		Iterable<AvailableRow> availableRows = availableRowRepo
				.findAllByShowIdAndSeatsLessThanOrderByRowCharDesc(showId, (1 << 10) - 1);
		logger.printf(Level.DEBUG, "Found %d available rows for show %d%n", IterableUtils.size(availableRows), showId);
		for (AvailableRow r : availableRows) {
			logger.printf(Level.DEBUG, "Found availableRow %c-%s%n", r.getRowChar(),
					Integer.toBinaryString(r.getSeats()));
		}
		List<AvailableRow> rowsToBook = new ArrayList<>();
		for (AvailableRow row : availableRows) {
			int availableSeats = row.getSeats();
			int bookingSeats = bookingSeatsInRows.get(row.getRowChar());
			if ((availableSeats & bookingSeats) != 0) {
				throw new IllegalStateException(
						"One of the seats has been booked or blocked, please check availability and try again");
			}
			bookedRows.add(new BookedRow(row, bookingSeats));
			row.setSeats(availableSeats ^ bookingSeats);
			rowsToBook.add(row);
		}
		show.setAvailableSeats(show.getAvailableSeats() - numOfSeats);
		Ticket t = new Ticket(show, phoneNum, LocalDateTime.now(), numOfSeats, bookedRows);
		showRepo.save(show);
		bookedRowRepo.saveAll(bookedRows);
		return ticketRepo.save(t);
	}

	private Map<Character, Integer> getBookingSeatsInRows(String csSeats) throws IllegalStateException {
		Map<Character, Integer> bookingSeatsInRows = new TreeMap<>();
		int totalSeatsToBook = 0;
		String[] seats = csSeats.split(",");
		for (String seat : seats) {
			char rowChar = seat.toLowerCase().charAt(0);
			if (rowChar < 'a' || rowChar > 'z') {
				throw new IllegalStateException("Row of " + rowChar + " no recognized, abort Booking");
			}
			int toBook = Integer.valueOf(seat.substring(1));
			if (toBook < 1 || toBook > 10) {
				throw new IllegalStateException(
						"Seat number of " + seat + " exceeds limits between 1 and 10, abort Booking");
			}
			int bookingSeat = bookingSeatsInRows.getOrDefault(rowChar, 0);
			bookingSeat |= (1 << (toBook - 1));
			totalSeatsToBook++;
			bookingSeatsInRows.put(rowChar, bookingSeat);
		}
		bookingSeatsInRows.put('.', totalSeatsToBook);
		logger.debug(bookingSeatsInRows.entrySet());
		return bookingSeatsInRows;
	}

	public boolean cancelTicket(long showId, long ticketId, String phoneNum) throws IllegalStateException {
		LocalDateTime time = LocalDateTime.now();
		Show show = showRepo.findById(showId)
				.orElseThrow(() -> new IllegalStateException("The Show with Id " + showId + " does not exist"));
		Ticket ticket = ticketRepo.findByIdAndPhoneNum(ticketId, phoneNum)
				.orElseThrow(() -> new IllegalStateException("The Ticket with Id " + ticketId + " does not exist"));
		if (time.isAfter(ticket.getBookedTime().plusMinutes(show.getCancelWindow()))) {
			throw new IllegalStateException("Cancel Window of " + show.getCancelWindow()
					+ " minutes has passed, you cannot cancel this ticket anymore");
		}
		List<AvailableRow> rowsToUpdate = new ArrayList<>();
		for (BookedRow bRow : ticket.getBookedRow()) {
			AvailableRow aRow = bRow.getRow();
			aRow.setSeats(aRow.getSeats() ^ bRow.getSeats());
			rowsToUpdate.add(aRow);
		}
		show.setAvailableSeats(show.getAvailableSeats() + ticket.getNumOfSeats());
		showRepo.save(show);
		availableRowRepo.saveAll(rowsToUpdate);
		ticketRepo.delete(ticket);
		bookedRowRepo.deleteAll(ticket.getBookedRow());
		return true;
	}
}
