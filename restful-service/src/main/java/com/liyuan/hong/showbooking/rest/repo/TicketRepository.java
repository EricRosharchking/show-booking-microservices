package com.liyuan.hong.showbooking.rest.repo;

import org.springframework.data.repository.CrudRepository;

import com.liyuan.hong.showbooking.rest.entity.Show;

public interface TicketRepository extends CrudRepository<Show, Long>{

}
