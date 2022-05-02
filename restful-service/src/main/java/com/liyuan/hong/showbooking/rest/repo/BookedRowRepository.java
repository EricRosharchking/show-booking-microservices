package com.liyuan.hong.showbooking.rest.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.liyuan.hong.showbooking.rest.domain.BookedRow;
import com.liyuan.hong.showbooking.rest.domain.Show;

@RepositoryRestResource(exported = false)
public interface BookedRowRepository extends CrudRepository<BookedRow, Long>{

}
