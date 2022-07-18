package com.imdb.service.dataimport.repository;

import com.imdb.service.dataimport.domain.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category,Integer> {

  /**
   * Find By Name
   * @param name
   * @return Category
   */
  Category findCategoryByNameIgnoreCase(String name);

  /**
   * Is Exists By Name
   * @param name
   * @return boolean
   */
  boolean existsByNameIgnoreCase(String name);
}
