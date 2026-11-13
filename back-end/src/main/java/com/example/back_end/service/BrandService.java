package com.example.back_end.service;

import java.util.List;

import com.example.back_end.model.Brand;

public interface BrandService {

    Brand createBrand(Brand brand);

    List<Brand> getAllBrands();

    Brand getBrandById(Long id);

    Brand updateBrand(Long id, Brand brand);

    void deleteBrand(Long id);
}
