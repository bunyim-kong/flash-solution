package com.example.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back_end.model.Product;
import java.util.List;
import java.util.Collection;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids order by p.id")
    List<Product> lockAllById(@Param("ids") Collection<Long> ids);

    boolean existsBySkuIgnoreCase(String sku);

    List<Product> findAllByOrderByNameAsc();
}
