package com.imdb.service.dataimport.service;

import com.imdb.service.dataimport.domain.CrewType;
import com.imdb.service.dataimport.repository.CrewTypeRepository;
import java.util.Optional;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log
@Service
public class CrewTypeService {

  @Autowired
  private CrewTypeRepository crewTypeRepository;

  /**
   * Save
   * @param crewType
   * @return CrewType
   */
  public CrewType save(CrewType crewType) {
    try {
      return crewTypeRepository.save(crewType);
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Find By Id
   * @param id
   * @return CrewType
   */
  public CrewType findById(Integer id) {
    try {
      Optional<CrewType> optional = crewTypeRepository.findById(id);
      return optional.isPresent() ? optional.get() : null;
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Find By Name
   * @param name
   * @return CrewType
   */
  public CrewType findByName(String name) {
    try {
      return crewTypeRepository.findCrewTypeByNameIgnoreCase(name);
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Is Exists By Name
   * @param name
   * @return boolean
   */
  public boolean existByName(String name) {
    try {
      return crewTypeRepository.existsByNameIgnoreCase(name);
    } catch (Exception e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      throw e;
    }
  }
}
