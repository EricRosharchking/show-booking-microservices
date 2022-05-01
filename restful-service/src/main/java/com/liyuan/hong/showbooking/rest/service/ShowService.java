package com.liyuan.hong.showbooking.rest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

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

	private List<BlockedRow> initBlockedRowsForShow(Show show, int rows, int seats) {
		List<BlockedRow> blockedRows = new ArrayList<>();
		for (int i = rows - 1; i < MAX_ROWS; i++) {
			BlockedRow blocked = new BlockedRow(show, (char) ('a' + i));
			blocked.setSeats((1 << MAX_SEATS) - 1);
			blockedRows.add(blocked);
		}
		if (seats <= MAX_SEATS) {
			for (int i = 0; i < rows; i++) {
				BlockedRow blocked = new BlockedRow(show, (char) ('a' + i));
				blocked.setSeats((1 << MAX_SEATS) - (1 << seats));
				blockedRows.add(blocked);
			}
		}
		return blockedRows;
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

	public boolean removeSeatsFromShow(long showId, int seats) {
		Show show = findShow(showId)
				.orElseThrow(() -> new RuntimeException("The Show with Id " + showId + " does not exist"));
		if (show.getAvailableSeats() < seats) {
			return false;
		}
		blockSeatsForShow(showId);
		return true;
	}

	private void blockSeatsForShow(long showId) {
		// TODO Auto-generated method stub

	}

	public boolean addRowsToShow(long showId, int rows) {
		List<BlockedRow> blockedRows = blockedRowRepo.findAllByShowIdAndSeatsIs(showId, 1 << 10 - 1);
		if (blockedRows.size() >= rows) {
			List<BlockedRow> addedRows = new ArrayList<>();
			blockedRows.stream().forEach(r -> {
				r.setSeats(0);
				addedRows.add(r);
			});
			blockedRowRepo.saveAll(addedRows);
			return true;
		}
		return false;
	}

	public List<String> availablility(long showId) {
		Iterable<AvailableRow> rows = availableRowRepo.findAllByShowIdAndSeatsLessThan(showId, (1 << 10) - 1);
		logger.printf(Level.DEBUG, "Found %d available rows for show %d%n.", IterableUtils.size(rows), showId);
		List<String> resultList = new ArrayList<>();
		for (AvailableRow row : rows) {
			String str = rowSeatsToString(row.getRow(), row.getSeats());
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
