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
	private final int AVAILABLE_ROW = (2 << 10) - 2;

	private AvailableRowRepository availableRowRepo;
	private ShowRepository showRepo;

	@Autowired
	public ShowService(AvailableRowRepository availableRowsRepo, BookedRowRepository bookedRowRepo,
			ShowRepository showRepo, TicketRepository ticketRepo) {
		super();
		this.availableRowRepo = availableRowsRepo;
		this.showRepo = showRepo;
	}

	public Show setupShow(long showId, int rows, int seats, int cancelWindow) {
		if (showRepo.existsById(showId)) {
			throw new IllegalStateException("Show of Id " + showId + " already exists");
		} else if (rows > MAX_ROWS || rows < 1) {
			throw new IllegalStateException("Rows of " + rows + " exceeds limits of 0 ~" + MAX_ROWS);
		} else if (seats > MAX_SEATS || seats < 1) {
			throw new IllegalStateException("Seats of " + seats + " exceeds limits of 0 ~ " + MAX_SEATS + " per row");
		} else if (cancelWindow < 0) {
			throw new IllegalStateException("CancelWindow cannot be negative.");
		}
		Show show = showRepo.save(new Show(showId, rows, seats, rows * seats, cancelWindow));
		logger.printf(Level.DEBUG, "Show of Id [%d] saved.%n", showId);
		List<AvailableRow> availableRows = initAvailableRowsForShow(show, rows, seats);
		availableRowRepo.saveAll(availableRows);
		logger.printf(Level.DEBUG, "AvailableRows of Show [%d] initialized and saved.%n", showId);
		return show;
	}

	private List<AvailableRow> initAvailableRowsForShow(Show show, int rows, int seats) {
		List<AvailableRow> availableRows = new ArrayList<>();
		for (int i = rows; i < MAX_ROWS; i++) {
			AvailableRow available = new AvailableRow(show, (char) ('a' + i));
			available.setSeats((2 << MAX_SEATS) - 1);
			availableRows.add(available);
		}
		if (seats <= MAX_SEATS) {
			for (int i = 0; i < rows; i++) {
				AvailableRow available = new AvailableRow(show, (char) ('a' + i));
				available.setSeats((2 << MAX_SEATS) - (2 << seats));
				availableRows.add(available);
			}
		}
		for (AvailableRow row : availableRows) {
			logger.debug(row.toString());
		}
		return availableRows;
	}

	public Optional<Show> findShow(long showId) {
		return showRepo.findById(showId);
	}

	public boolean removeSeatsFromShow(long showId, int numOfSeatsToRemove) {
		Show show = findShow(showId)
				.orElseThrow(() -> new IllegalStateException("The Show with Id " + showId + " does not exist"));
		if (show.getAvailableSeats() < numOfSeatsToRemove) {
			return false;
		}
		blockSeatsForShow(showId, numOfSeatsToRemove);
		show.setAvailableSeats(show.getAvailableSeats() - numOfSeatsToRemove);
		showRepo.save(show);
		return true;
	}

	private void blockSeatsForShow(long showId, int numOfSeatsToRemove) {
		int i = numOfSeatsToRemove;
		List<AvailableRow> availableRows = StreamSupport.stream(
				availableRowRepo.findAllByShowIdAndSeatsLessThanOrderByRowCharDesc(showId, BLOCKED_ROW).spliterator(),
				false).collect(Collectors.toList());
		List<AvailableRow> newRows = new ArrayList<>();
		for (AvailableRow row : availableRows) {
			while (i > 0 && row.getSeats() < AVAILABLE_ROW) {
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
		availableRowRepo.saveAll(newRows);
	}

	public boolean addRowsToShow(long showId, int rows) {
		Show show = showRepo.findById(showId)
				.orElseThrow(() -> new IllegalStateException("The Show with Id " + showId + " does not exist"));
		int numOfRows = show.getNumOfRows();
		if (numOfRows + rows > MAX_ROWS) {
			throw new IllegalStateException(
					"The Show with Id " + showId + " will exceeds MAX_NUM_OF_ROWS if added [" + rows + "] rows to it");
		}
		int seatsPerRow = show.getSeatsPerRow();
		List<AvailableRow> availableRows = StreamSupport.stream(
				availableRowRepo.findAllByShowIdAndSeatsEqualsOrderByRowCharAsc(showId, BLOCKED_ROW).spliterator(),
				false).collect(Collectors.toList());
		if (availableRows.size() < rows) {
			throw new IllegalStateException("There is not enough rows to add for the show specified");
		}
		List<AvailableRow> addedRows = new ArrayList<>();
		Iterator<AvailableRow> iter = availableRows.iterator();
		for (int i = 0; i < rows; i++) {
			AvailableRow row = iter.next();
			row.setSeats((2 << MAX_SEATS) - (2 << seatsPerRow));
			addedRows.add(row);
		}
		show.setNumOfRows(show.getNumOfRows() + rows);
		show.setAvailableSeats(show.getAvailableSeats() + rows * seatsPerRow);
		showRepo.save(show);
		availableRowRepo.saveAll(addedRows);
		return true;
	}

	public List<String> availablility(long showId) {
		Iterable<AvailableRow> rows = availableRowRepo.findAllByShowIdAndSeatsLessThanOrderByRowCharDesc(showId,
				BLOCKED_ROW);
		logger.printf(Level.DEBUG, "Found %d available rows for show %d%n.", IterableUtils.size(rows), showId);
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
		if (seats == AVAILABLE_ROW) {
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
