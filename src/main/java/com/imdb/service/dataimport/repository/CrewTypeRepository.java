package com.imdb.service.dataimport.repository;

import com.imdb.service.dataimport.domain.CrewType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewTypeRepository extends CrudRepository<CrewType,Integer> {

  /**
   * Find By Name
   * @param name
   * @return CrewType
   */
  CrewType findCrewTypeByNameIgnoreCase(String name);

  /**
   * Is Exists By Name
   * @param name
   * @return boolean
   */
  boolean existsByNameIgnoreCase(String name);
}
