package com.liyuan.hong.showbooking.rest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liyuan.hong.showbooking.rest.domain.AvailableRow;
import com.liyuan.hong.showbooking.rest.domain.BlockedRow;
import com.liyuan.hong.showbooking.rest.domain.Show;
import com.liyuan.hong.showbooking.rest.repo.AvailableRowRepository;
import com.liyuan.hong.showbooking.rest.repo.BlockedRowRepository;
import com.liyuan.hong.showbooking.rest.repo.ShowRepository;
import com.liyuan.hong.showbooking.rest.repo.TicketRepository;

@Service
public class ShowService {

	private final int MAX_ROWS = 26;
	private final int MAX_SEATS = 10;
	private final int MAX_CAPACITY = 260;

	private AvailableRowRepository availableRowsRepo;
	private BlockedRowRepository blockedRowsRepo;
	private ShowRepository showRepo;
	private TicketRepository ticketRepo;

	@Autowired
	public ShowService(AvailableRowRepository availableRowsRepo, BlockedRowRepository blockedRowsRepo,
			ShowRepository showRepo, TicketRepository ticketRepo) {
		super();
		this.availableRowsRepo = availableRowsRepo;
		this.blockedRowsRepo = blockedRowsRepo;
		this.showRepo = showRepo;
		this.ticketRepo = ticketRepo;
	}

	public Show setupShow(long showId, int rows, int seats, int cancelWindow) {
		if (showRepo.existsById(showId)) {
			throw new IllegalStateException("Show of Id " + showId + " already exist");
		}
		Show show = showRepo
				.save(new Show(showId, rows, seats, rows * seats, MAX_CAPACITY - rows * seats, cancelWindow));
		Iterable<BlockedRow> blockedRows = initBlockedRowsForShow(show, rows, seats);
		Iterable<AvailableRow> availableRows = initAvailableRowsForShow(show, rows, seats);
		blockedRowsRepo.saveAll(blockedRows);
		availableRowsRepo.saveAll(availableRows);
		return show;
	}

	private Iterable<BlockedRow> initBlockedRowsForShow(Show show, int rows, int seats) {
		Iterable<BlockedRow> blockedRows = blockedRowsRepo.findAllByShowId(show.getId());
		if (rows < MAX_ROWS) {
			for (BlockedRow blocked : blockedRows) {
				blocked.setSeats((1 << MAX_SEATS) - 1);
			}
		}
		if (seats < MAX_SEATS) {
			for (BlockedRow blocked : blockedRows) {
				blocked.setSeats((1 << MAX_SEATS) - (1 << seats));
			}
		}
		return blockedRows;
	}

	private Iterable<AvailableRow> initAvailableRowsForShow(Show show, int rows, int seats) {
		Iterable<AvailableRow> available = availableRowsRepo.findAllByShowId(show.getId());

		return available;
	}

	public Optional<Show> viewShow(long showId) {
		return showRepo.findById(showId);
		// .orElseThrow(() -> new RuntimeException("The Show with Id " + showId + " does
		// not exist"));
	}

	public boolean removeSeatsFromShow(long showId, int seats) {
		Show show = viewShow(showId)
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

		return true;
	}
}
