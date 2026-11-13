package com.example.back_end.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // foreign key
    @ManyToOne
    @JoinColumn (name = "brand_id")
    private Brand brand;
    //

    private String name;

    private String imageUrl;

    //enum
    @Enumerated (EnumType.STRING)
    @Column (name = "product_status", nullable = false)
    private ProductStatus product_status = ProductStatus.AVAILABLE;
    //

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    private Integer stock;

    private String des;

    public Long getId() {
        return id;
    }

    // foreign key
    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
    //

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // enum
    public enum ProductStatus {
        AVAILABLE,
        OUT_OF_STOCK
    }
    //

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public String getDes() {
        return des;
    }
}
