package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back_end.model.Category;
public interface CategoryRepository extends JpaRepository<Category, Long> {

}