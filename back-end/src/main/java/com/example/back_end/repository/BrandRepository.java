package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back_end.model.Brand;
import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long>{
    boolean existsByNameIgnoreCase(String name);
    
    boolean existsBySlugIgnoreCase(String slug);

    List<Brand> findAllByOrderByNameAsc();
}
