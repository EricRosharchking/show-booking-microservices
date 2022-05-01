package com.liyuan.hong.showbooking.rest.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.liyuan.hong.showbooking.rest.domain.BlockedRow;

@RepositoryRestResource(exported = false)
public interface BlockedRowRepository extends CrudRepository<BlockedRow, Long>{

	Iterable<BlockedRow> findAllByShowId(Long id);

	List<BlockedRow> findAllByShowIdAndSeatsIs(long showId, int i);

}
