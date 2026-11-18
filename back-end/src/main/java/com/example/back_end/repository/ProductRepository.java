package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back_end.model.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>{
    boolean existsBySkuIgnoreCase(String sku);

    List<Product> findAllByOrderByNameAsc();
}
