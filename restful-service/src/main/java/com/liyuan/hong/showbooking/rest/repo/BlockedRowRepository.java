package com.liyuan.hong.showbooking.rest.repo;

import org.springframework.data.repository.CrudRepository;

import com.liyuan.hong.showbooking.rest.domain.BlockedRow;

public interface BlockedRowRepository extends CrudRepository<BlockedRow, Long>{

	Iterable<BlockedRow> findAllByShowId(Long id);

}
