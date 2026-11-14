
package com.example.back_end.repository;

import com.example.back_end.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByNameAsc();
    
    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlugIgnoreCase(String slug);

}