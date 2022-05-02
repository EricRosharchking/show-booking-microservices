package com.liyuan.hong.showbooking.rest.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.liyuan.hong.showbooking.rest.domain.AvailableRow;

@RepositoryRestResource(exported = false)
public interface AvailableRowRepository extends CrudRepository<AvailableRow, Long>{

	Iterable<AvailableRow> findAllByShowId(Long id);

	Iterable<AvailableRow> findAllByShowIdAndSeatsLessThan(long showId, int i);

	Iterable<AvailableRow> findAllByShowIdAndSeatsIs(long showId, int i);

}
