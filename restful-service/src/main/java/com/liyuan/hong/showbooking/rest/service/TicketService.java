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
import org.springframework.transaction.annotation.Transactional;

import com.liyuan.hong.showbooking.rest.domain.ShowRow;
import com.liyuan.hong.showbooking.rest.domain.BookedRow;
import com.liyuan.hong.showbooking.rest.domain.Show;
import com.liyuan.hong.showbooking.rest.domain.Ticket;
import com.liyuan.hong.showbooking.rest.repo.ShowRowRepository;
import com.liyuan.hong.showbooking.rest.repo.BookedRowRepository;
import com.liyuan.hong.showbooking.rest.repo.ShowRepository;
import com.liyuan.hong.showbooking.rest.repo.TicketRepository;

@Service
@Transactional
public class TicketService {

	private final int FULLY_BOOKED_ROW = (2 << 10) - 2;

	Logger logger = LogManager.getLogger(this.getClass());

	private ShowRowRepository availableRowRepo;
	private BookedRowRepository bookedRowRepo;
	private ShowRepository showRepo;
	private TicketRepository ticketRepo;

	@Autowired
	public TicketService(ShowRowRepository availableRowsRepo, BookedRowRepository bookedRowRepo,
			ShowRepository showRepo, TicketRepository ticketRepo) {
		super();
		this.availableRowRepo = availableRowsRepo;
		this.bookedRowRepo = bookedRowRepo;
		this.showRepo = showRepo;
		this.ticketRepo = ticketRepo;
	}

	public List<Ticket> viewBookedTicketsOfShow(long showId) {
		return IterableUtils.toList(ticketRepo.findAllByShowId(showId));
	}

	public Ticket bookTicket(long showId, String phoneNum, String csSeats)
			throws IllegalStateException, DataIntegrityViolationException {
		Show show = findShowOrThrowError(showId);
		Map<Character, Integer> bookingSeatsInRowChars = getBookingSeatsForRowChars(csSeats);
		int numOfSeats = getTotalNumOfSeatsToBook(bookingSeatsInRowChars);
		throwErrorIfNotEnoughSeatsToBook(show, numOfSeats);
		Iterable<ShowRow> availableRows = findAvailableRowsToBookAndLog(showId, bookingSeatsInRowChars.keySet());
		Set<BookedRow> bookedRows = getBookedRows(availableRows, bookingSeatsInRowChars);
		List<ShowRow> rowsToBook = getAvailableRowsToBook(availableRows, bookingSeatsInRowChars);
		show.setAvailableSeats(show.getAvailableSeats() - numOfSeats);
		show = showRepo.save(show);
		Ticket t = new Ticket(show, phoneNum, LocalDateTime.now(), numOfSeats, bookedRows);
		bookedRowRepo.saveAll(bookedRows);
		availableRowRepo.saveAll(rowsToBook);
		return ticketRepo.save(t);
	}

	private Show findShowOrThrowError(long showId) {
		return showRepo.findById(showId)
				.orElseThrow(() -> new IllegalStateException("The Show with Id " + showId + " does not exist"));
	}

	private Map<Character, Integer> getBookingSeatsForRowChars(String csSeats) throws IllegalStateException {
		Map<Character, Integer> bookingSeatsInRows = new TreeMap<>();
		String[] seats = csSeats.split(",");
		for (String seat : seats) {
			char rowChar = seat.toLowerCase().charAt(0);
			int toBook = Integer.valueOf(seat.substring(1));
			throwErrorIfRowAndSeatNotValid(rowChar, toBook);
			int bookingSeat = bookingSeatsInRows.getOrDefault(rowChar, 0);
			bookingSeat |= (1 << toBook);
			bookingSeatsInRows.put(rowChar, bookingSeat);
		}
		logger.debug(bookingSeatsInRows.entrySet());
		return bookingSeatsInRows;
	}

	private void throwErrorIfRowAndSeatNotValid(char rowChar, int toBook) {
		if (rowChar < 'a' || rowChar > 'z') {
			throw new IllegalStateException("Row of " + rowChar + " no recognized");
		}
		if (toBook < 1 || toBook > 10) {
			throw new IllegalStateException(
					"Seat number of " + toBook + " exceeds limits between 1 and 10");
		}
	}

	private int getTotalNumOfSeatsToBook(Map<Character, Integer> bookingSeatsInRows) {
		int totalNum = 0;
		for (int num : bookingSeatsInRows.values()) {
			for (int i = 1; i <= 10; i++) {
				if (((1 << i) & num) != 0) {
					totalNum++;
				}
			}
		}
		logger.debug("Total Number of Seats to book is: [" + totalNum + "]");
		return totalNum;
	}

	private void throwErrorIfNotEnoughSeatsToBook(Show show, int numOfSeats) {
		if (show.getAvailableSeats() < numOfSeats) {
			throw new IllegalStateException(
					"There are not enough available seats to book, please check availability and try again");
		}
	}

	private Iterable<ShowRow> findAvailableRowsToBookAndLog(long showId, Set<Character> rowChars) {
		Iterable<ShowRow> availableRows = availableRowRepo
				.findAllByShowIdAndSeatsLessThanAndRowCharInOrderByRowCharDesc(showId, FULLY_BOOKED_ROW, rowChars);
		logger.printf(Level.DEBUG, "Found %d available rows for show %d%n", IterableUtils.size(availableRows), showId);
		for (ShowRow r : availableRows) {
			logger.printf(Level.DEBUG, "Found availableRow %c-%s%n", r.getRowChar(),
					Integer.toBinaryString(r.getSeats()));
		}
		return availableRows;
	}

	private Set<BookedRow> getBookedRows(Iterable<ShowRow> availableRows,
			Map<Character, Integer> bookingSeatsInRowChars) {
		Set<BookedRow> bookedRows = new HashSet<>();
		for (ShowRow row : availableRows) {
			int availableSeats = row.getSeats();
			int bookingSeats = bookingSeatsInRowChars.get(row.getRowChar());
			throwErrorIfBookedOrBlocked(availableSeats, bookingSeats, row);
			bookedRows.add(new BookedRow(row, bookingSeats));
		}
		return bookedRows;
	}

	private void throwErrorIfBookedOrBlocked(int availableSeats, int bookingSeats, ShowRow row) {
		if ((availableSeats & bookingSeats) != 0) {
			logger.printf(Level.DEBUG, "AvailableRow is %c-%s, bookingSeats is %s%n", row.getRowChar(),
					Integer.toBinaryString(row.getSeats()), Integer.toBinaryString(bookingSeats));
			throw new IllegalStateException(
					"One of the seats has been booked or blocked, please check availability and try again");
		}
	}

	private List<ShowRow> getAvailableRowsToBook(Iterable<ShowRow> availableRows,
			Map<Character, Integer> bookingSeatsInRowChars) {
		List<ShowRow> rowsToBook = new ArrayList<>();
		for (ShowRow row : availableRows) {
			int availableSeats = row.getSeats();
			int bookingSeats = bookingSeatsInRowChars.get(row.getRowChar());
			throwErrorIfBookedOrBlocked(availableSeats, bookingSeats, row);
			row.setSeats(availableSeats ^ bookingSeats);
			rowsToBook.add(row);
		}
		return rowsToBook;
	}

	public boolean cancelTicket(long showId, long ticketId, String phoneNum) throws IllegalStateException {
		LocalDateTime time = LocalDateTime.now();
		Show show = findShowOrThrowError(showId);
		Ticket ticket = findTicketOrThrowError(ticketId, phoneNum);
		throwErrorIfPassCancelWindow(time, ticket, show);
		show.setAvailableSeats(show.getAvailableSeats() + ticket.getNumOfSeats());
		showRepo.save(show);
		availableRowRepo.saveAll(getRowsToUpdate(ticket));
		ticketRepo.delete(ticket);
		bookedRowRepo.deleteAll(ticket.getBookedRow());
		return true;
	}

	private Ticket findTicketOrThrowError(long ticketId, String phoneNum) {
		return ticketRepo.findByIdAndPhoneNum(ticketId, phoneNum)
				.orElseThrow(() -> new IllegalStateException("The Ticket with Id " + ticketId + " does not exist"));
	}

	private void throwErrorIfPassCancelWindow(LocalDateTime time, Ticket t, Show s) {
		if (time.isAfter(t.getBookedTime().plusMinutes(s.getCancelWindow()))) {
			throw new IllegalStateException("Cancel Window of " + s.getCancelWindow()
					+ " minutes has passed, you cannot cancel this ticket anymore");
		}
	}

	private List<ShowRow> getRowsToUpdate(Ticket t) {
		List<ShowRow> rowsToUpdate = new ArrayList<>();
		for (BookedRow bRow : t.getBookedRow()) {
			ShowRow aRow = bRow.getRow();
			aRow.setSeats(aRow.getSeats() ^ bRow.getSeats());
			rowsToUpdate.add(aRow);
		}
		return rowsToUpdate;
	}
}
