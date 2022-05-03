package com.liyuan.hong.showbooking.rest.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.IterableUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liyuan.hong.showbooking.rest.domain.AvailableRow;
import com.liyuan.hong.showbooking.rest.domain.Show;
import com.liyuan.hong.showbooking.rest.repo.AvailableRowRepository;
import com.liyuan.hong.showbooking.rest.repo.BookedRowRepository;
import com.liyuan.hong.showbooking.rest.repo.ShowRepository;
import com.liyuan.hong.showbooking.rest.repo.TicketRepository;

@Service
@Transactional
public class ShowService {

	Logger logger = LogManager.getLogger(this.getClass());

	private final int MAX_ROWS = 26;
	private final int MAX_SEATS = 10;
	private final int BLOCKED_ROW = (2 << 10) - 1;
	private final int FULLY_BOOKED_ROW = (2 << 10) - 2;

	private AvailableRowRepository availableRowRepo;
	private ShowRepository showRepo;

	@Autowired
	public ShowService(AvailableRowRepository availableRowsRepo, BookedRowRepository bookedRowRepo,
			ShowRepository showRepo, TicketRepository ticketRepo) {
		super();
		this.availableRowRepo = availableRowsRepo;
		this.showRepo = showRepo;
	}

	public Show setupShow(long showId, int rows, int seats, int cancelWindow) throws IllegalStateException {
		throwErrorIfSetupRequestIsNotValid(showId, rows, seats, cancelWindow);
		Show show = showRepo.save(new Show(showId, rows, seats, rows * seats, cancelWindow));
		logger.printf(Level.DEBUG, "Show of Id [%d] saved.%n", showId);
		availableRowRepo.saveAll(initAllRowsForShow(show, rows, seats));
		logger.printf(Level.DEBUG, "AvailableRows of Show [%d] initialized and saved.%n", showId);
		return show;
	}
	
	private void throwErrorIfSetupRequestIsNotValid(long showId, int rows, int seats, int cancelWindow) {
		if (findShow(showId).isPresent()) {
			throw new IllegalStateException("Show of Id " + showId + " already exists");
		} else if (rows > MAX_ROWS || rows < 1) {
			throw new IllegalStateException("Rows of " + rows + " exceeds limits of 1 ~" + MAX_ROWS);
		} else if (seats > MAX_SEATS || seats < 1) {
			throw new IllegalStateException("Seats of " + seats + " exceeds limits of 1 ~ " + MAX_SEATS + " per row");
		} else if (cancelWindow < 0) {
			throw new IllegalStateException("CancelWindow cannot be negative.");
		}
	}

	private List<AvailableRow> initAllRowsForShow(Show show, int rows, int seats) {
		List<AvailableRow> availableRows = new ArrayList<>();
		availableRows.addAll(initBlockedRows(show, rows, seats));
		availableRows.addAll(initAvailableRows(show, rows, seats));
		for (AvailableRow row : availableRows) {
			logger.debug(row.toString());
		}
		return availableRows;
	}

	private List<AvailableRow> initBlockedRows(Show show, int rows, int seats) {
		List<AvailableRow> blockedRows = new ArrayList<>();
		for (int i = rows; i < MAX_ROWS; i++) {
			AvailableRow blockedRow = new AvailableRow(show, (char) ('a' + i));
			blockedRow.setSeats((2 << MAX_SEATS) - 1);
			blockedRows.add(blockedRow);
		}
		return blockedRows;
	}

	private List<AvailableRow> initAvailableRows(Show show, int rows, int seats) {
		List<AvailableRow> availableRows = new ArrayList<>();
		if (seats <= MAX_SEATS) {
			for (int i = 0; i < rows; i++) {
				AvailableRow availableRow = new AvailableRow(show, (char) ('a' + i));
				availableRow.setSeats((2 << MAX_SEATS) - (2 << seats));
				availableRows.add(availableRow);
			}
		}
		return availableRows;
	}

	private Optional<Show> findShow(long showId) {
		return showRepo.findById(showId);
	}

	public boolean removeSeatsFromShow(long showId, int numOfSeatsToRemove) throws IllegalStateException {
		Show show = findShowOrThrowErrorIfNotFound(showId);
		throwErrorIfNotEnoughSeatsToRemove(show, numOfSeatsToRemove);
		show.setAvailableSeats(show.getAvailableSeats() - numOfSeatsToRemove);
		showRepo.save(show);
		availableRowRepo.saveAll(getRowsToUpdate(showId, numOfSeatsToRemove));
		return true;
	}

	private Show findShowOrThrowErrorIfNotFound(long showId) {
		return findShow(showId)
				.orElseThrow(() -> new IllegalStateException("The Show with Id " + showId + " does not exist"));
	}
	
	private void throwErrorIfNotEnoughSeatsToRemove(Show show, int numOfSeatsToRemove) {
		if (show.getAvailableSeats() < numOfSeatsToRemove) {
			throw new IllegalStateException("The Show with Id " + show.getId() + " does not have enough seats to remove");
		}
	}

	private List<AvailableRow> getRowsToUpdate(long showId, int numOfSeatsToRemove) {
		int i = numOfSeatsToRemove;
		List<AvailableRow> oldRows = StreamSupport.stream(
				availableRowRepo.findAllByShowIdAndSeatsLessThanOrderByRowCharDesc(showId, BLOCKED_ROW).spliterator(),
				false).collect(Collectors.toList());
		List<AvailableRow> newRows = new ArrayList<>();
		for (AvailableRow row : oldRows) {
			while (i > 0 && row.getSeats() < FULLY_BOOKED_ROW) {
				int seat = 0;
				for (int j = 0; j < 10 && i > 0; j++) {
					if (((2 << j) & row.getSeats()) == 0) {
						i--;
						seat |= (2 << j);
					}
				}
				logger.debug("For row [" + showId + ":" + row.getRowChar() + "], Blocked seats " + seat
						+ ", original seats " + row.getSeats());
				row.setSeats(seat ^ row.getSeats());
				newRows.add(row);
			}
		}
		return newRows;
	}

	public boolean addRowsToShow(long showId, int rows) throws IllegalStateException {
		Show show = findShowOrThrowErrorIfNotFound(showId);
		throwErrorIfRowsCanBeAddedToShow(rows, show);
		List<AvailableRow> addedRows = getUnblockedRows(show, rows);
		show.setNumOfRows(show.getNumOfRows() + rows);
		show.setAvailableSeats(show.getAvailableSeats() + rows * show.getSeatsPerRow());
		showRepo.save(show);
		availableRowRepo.saveAll(addedRows);
		return true;
	}

	private void throwErrorIfRowsCanBeAddedToShow(int rows, Show show) {
		int numOfRows = show.getNumOfRows();
		if (numOfRows + rows > MAX_ROWS) {
			throw new IllegalStateException("The Show with Id " + show.getId()
					+ " will exceeds MAX_NUM_OF_ROWS if added [" + rows + "] rows to it");
		}
	}

	private List<AvailableRow> getUnblockedRows(Show show, int rows) {
		List<AvailableRow> unblockedRows = new ArrayList<>();
		List<AvailableRow> blockedRows = StreamSupport.stream(availableRowRepo
				.findAllByShowIdAndSeatsEqualsOrderByRowCharAsc(show.getId(), BLOCKED_ROW).spliterator(), false)
				.collect(Collectors.toList());
		Iterator<AvailableRow> iter = blockedRows.iterator();
		for (int i = 0; i < rows; i++) {
			AvailableRow row = iter.next();
			row.setSeats((2 << MAX_SEATS) - (2 << show.getSeatsPerRow()));
			unblockedRows.add(row);
		}
		return unblockedRows;
	}

	public List<String> seatsAvailablilityOfShow(long showId) throws IllegalStateException {
		Iterable<AvailableRow> rows = availableRowRepo.findAllByShowIdAndSeatsLessThanOrderByRowCharDesc(showId,
				BLOCKED_ROW);
		logger.printf(Level.DEBUG, "Found %d available rows for show %d%n.", IterableUtils.size(rows), showId);
		List<String> resultList = getAvailableSeatsStrings(rows);
		return resultList;
	}

	private List<String> getAvailableSeatsStrings(Iterable<AvailableRow> rows) {
		List<String> resultList = new ArrayList<>();
		for (AvailableRow row : rows) {
			String str = rowSeatsToString(row.getRowChar(), row.getSeats());
			if (!str.isEmpty()) {
				logger.printf(Level.DEBUG, "[%s] added to result", str);
				resultList.add(str);
			}
		}
		return resultList;
	}

	private String rowSeatsToString(char row, int seats) {
		StringBuilder sb = new StringBuilder();
		if (seats == FULLY_BOOKED_ROW) {
			return "";
		}
		for (int i = 0; i < 10; i++) {
			int seat = 2 << i;
			if ((seat & seats) != (seat)) {
				sb.append(row).append(i + 1).append(",");
			}
		}
		return sb.replace(sb.length() - 1, sb.length(), ".").toString();
	}
}
