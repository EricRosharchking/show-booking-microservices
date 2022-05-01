package com.liyuan.hong.showbooking.rest.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.liyuan.hong.showbooking.rest.domain.Show;
import com.liyuan.hong.showbooking.rest.domain.Ticket;

@RepositoryRestResource(exported = false)
public interface TicketRepository extends CrudRepository<Ticket, Long>{

	Iterable<Ticket> findAllByShowId(long showId);

}
