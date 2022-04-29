package com.liyuan.hong.showbooking.rest.repo;

import org.springframework.data.repository.CrudRepository;

import com.liyuan.hong.showbooking.rest.domain.AvailableRow;

public interface AvailableRowRepository extends CrudRepository<AvailableRow, Long>{

	Iterable<AvailableRow> findAllByShowId(Long id);

}
