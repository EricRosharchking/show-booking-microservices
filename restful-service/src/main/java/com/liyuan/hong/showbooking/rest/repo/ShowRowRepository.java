package com.liyuan.hong.showbooking.rest.repo;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.liyuan.hong.showbooking.rest.domain.ShowRow;

@RepositoryRestResource(exported = false)
public interface ShowRowRepository extends CrudRepository<ShowRow, Long>{

	Iterable<ShowRow> findAllByShowId(Long id);

	Iterable<ShowRow> findAllByShowIdAndSeatsLessThanOrderByRowCharDesc(long showId, int seats);

	Iterable<ShowRow> findAllByShowIdAndSeatsEquals(long showId, int seats);

	Iterable<ShowRow> findAllByShowIdOrderByRowCharAsc(long showId);

	Iterable<ShowRow> findAllByShowIdAndSeatsEqualsOrderByRowCharAsc(long showId, int i);

	Iterable<ShowRow> findAllByShowIdAndSeatsLessThanAndRowCharInOrderByRowCharDesc(long showId, int aVAILABLE_ROW,
			Set<Character> rowChars);

}
