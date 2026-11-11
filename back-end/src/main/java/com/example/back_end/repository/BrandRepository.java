package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back_end.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long>{
    
}
