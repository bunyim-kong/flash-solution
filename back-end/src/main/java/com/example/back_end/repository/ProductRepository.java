package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back_end.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    
}
