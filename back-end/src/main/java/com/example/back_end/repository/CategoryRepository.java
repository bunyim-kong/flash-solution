package com.example.back_end.repository;

import java.util.List;

import com.example.back_end.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlugIgnoreCase(String slug);

    List<Category> findAllByOrderByNameAsc();
}