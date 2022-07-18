package com.imdb.service.dataimport.repository;

import com.imdb.service.dataimport.domain.TitleType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleTypeRepository extends CrudRepository<TitleType,Integer> {

  /**
   * Find By Name
   * @param name
   * @return TitleType
   */
  TitleType findTitleTypeByNameIgnoreCase(String name);

  /**
   * Is Exists By Name
   * @param name
   * @return boolean
   */
  boolean existsByNameIgnoreCase(String name);
}
