package com.liyuan.hong.showbooking.rest.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.IterableUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liyuan.hong.showbooking.rest.domain.AvailableRow;
import com.liyuan.hong.showbooking.rest.domain.BlockedRow;
import com.liyuan.hong.showbooking.rest.domain.Show;
import com.liyuan.hong.showbooking.rest.domain.Ticket;
import com.liyuan.hong.showbooking.rest.repo.AvailableRowRepository;
import com.liyuan.hong.showbooking.rest.repo.BlockedRowRepository;
import com.liyuan.hong.showbooking.rest.repo.ShowRepository;
import com.liyuan.hong.showbooking.rest.repo.TicketRepository;

@Service
public class ShowService {

	Logger logger = LogManager.getLogger(this.getClass());

	private final int MAX_ROWS = 26;
	private final int MAX_SEATS = 10;
	private final int MAX_CAPACITY = 260;

	private AvailableRowRepository availableRowRepo;
	private BlockedRowRepository blockedRowRepo;
	private ShowRepository showRepo;
	private TicketRepository ticketRepo;

	@Autowired
	public ShowService(AvailableRowRepository availableRowsRepo, BlockedRowRepository blockedRowsRepo,
			ShowRepository showRepo, TicketRepository ticketRepo) {
		super();
		this.availableRowRepo = availableRowsRepo;
		this.blockedRowRepo = blockedRowsRepo;
		this.showRepo = showRepo;
		this.ticketRepo = ticketRepo;
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
		Show show = showRepo
				.save(new Show(showId, rows, seats, rows * seats, MAX_CAPACITY - rows * seats, cancelWindow));
		logger.printf(Level.DEBUG, "Show of Id [%d] saved.%n", showId);
//		Iterable<BlockedRow> blockedRows = initBlockedRowsForShow(show, rows, seats);
		List<AvailableRow> availableRows = initAvailableRowsForShow(show, rows, seats);
//		blockedRowRepo.saveAll(blockedRows);
		availableRowRepo.saveAll(availableRows);
		logger.printf(Level.DEBUG, "AvailableRows of Show [%d] initialized and saved.%n", showId);
		return show;
	}

	private List<AvailableRow> initAvailableRowsForShow(Show show, int rows, int seats) {
		List<AvailableRow> availableRows = new ArrayList<>();
		for (int i = rows; i < MAX_ROWS; i++) {
			AvailableRow available = new AvailableRow(show, (char) ('a' + i));
			available.setSeats((1 << MAX_SEATS) - 1);
			availableRows.add(available);
		}
		if (seats <= MAX_SEATS) {
			for (int i = 0; i < rows; i++) {
				AvailableRow available = new AvailableRow(show, (char) ('a' + i));
				available.setSeats((1 << MAX_SEATS) - (1 << seats));
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
		// .orElseThrow(() -> new RuntimeException("The Show with Id " + showId + " does
		// not exist"));
	}

	public List<Ticket> viewShow(long showId) {
		return IterableUtils.toList(ticketRepo.findAllByShowId(showId));
	}

	public boolean removeSeatsFromShow(long showId, int numOfSeats) {
		Show show = findShow(showId)
				.orElseThrow(() -> new IllegalStateException("The Show with Id " + showId + " does not exist"));
		if (show.getAvailableSeats() < numOfSeats) {
			return false;
		}
		blockSeatsForShow(showId, numOfSeats);
		show.setAvailableSeats(numOfSeats);
		show.setBlockedSeats(numOfSeats);
		showRepo.save(show);
		return true;
	}

	private void blockSeatsForShow(long showId, int numOfSeats) {
		int i = numOfSeats;
		List<AvailableRow> availableRows = StreamSupport.stream(
				availableRowRepo.findAllByShowIdAndSeatsLessThanOrderByRowCharDesc(showId, (1 << 10) - 1).spliterator(),
				false).collect(Collectors.toList());
		List<AvailableRow> newRows = new ArrayList<>();
		for (AvailableRow row : availableRows) {
			while (i > 0 && row.getSeats() < (1 << 10) - 1) {
				int seat = 0;
				for (int j = 0; j < 10 && i > 0; j++) {
					if (((1 << j) & row.getSeats()) == 0) {
						i--;
						seat |= (1 << j);
					}
				}
				logger.debug("For row [" + row.getRowId() + "], Blocked seats " + seat + ", original seats "
						+ row.getSeats());
				row.setSeats(seat ^ row.getSeats());
				newRows.add(row);
			}
		}
		availableRowRepo.saveAll(newRows);
	}

	public boolean addRowsToShow(long showId, int rows) {
		int seats = showRepo.findById(showId)
				.orElseThrow(() -> new IllegalStateException("The Show with Id " + showId + " does not exist"))
				.getSeats();
		List<AvailableRow> availableRows = StreamSupport.stream(
				availableRowRepo.findAllByShowIdAndSeatsEqualsOrderByRowCharAsc(showId, (1 << 10) - 1).spliterator(),
				false).collect(Collectors.toList());
		if (availableRows.size() < rows) {
			throw new IllegalStateException("There is not enough rows to add for the show specified");
		}
		List<AvailableRow> addedRows = new ArrayList<>();
		Iterator<AvailableRow> iter = availableRows.iterator();
		for (int i = 0; i < rows; i++) {
			AvailableRow row = iter.next();
			row.setSeats((1 << MAX_SEATS) - (1 << seats));
			addedRows.add(row);
		}
		availableRowRepo.saveAll(addedRows);
		return true;
	}

	public List<String> availablility(long showId) {
		Iterable<AvailableRow> rows = availableRowRepo.findAllByShowIdAndSeatsLessThanOrderByRowCharDesc(showId,
				(1 << 10) - 1);
		logger.printf(Level.DEBUG, "Found %d available rows for show %d%n.", IterableUtils.size(rows), showId);
		List<String> resultList = new ArrayList<>();
		for (AvailableRow row : rows) {
			String str = rowSeatsToString(row.getRowChar(), row.getSeats());
			logger.printf(Level.DEBUG, "[%s] added to result", str);
			resultList.add(str);
		}
		return resultList;
	}

	private String rowSeatsToString(char row, int seats) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			int seat = 1 << i;
			if ((seat & seats) != (seat)) {
				sb.append(row).append(i + 1).append(",");
			}
		}
		return sb.replace(sb.length() - 1, sb.length(), ".").toString();
	}
}
