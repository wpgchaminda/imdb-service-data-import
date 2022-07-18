package com.imdb.service.dataimport.repository;

import com.imdb.service.dataimport.domain.Title;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleRepository extends CrudRepository<Title,String> {
}
