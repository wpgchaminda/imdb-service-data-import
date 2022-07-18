package com.imdb.service.dataimport.repository;

import com.imdb.service.dataimport.domain.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person,String> {
}
