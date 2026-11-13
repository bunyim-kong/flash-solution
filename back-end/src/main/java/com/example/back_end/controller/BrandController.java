package com.example.back_end.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.back_end.model.Brand;
import com.example.back_end.service.BrandService;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Brand create(@RequestBody Brand brand) {
        return brandService.createBrand(brand);
    }

    // READ (all)
    @GetMapping
    public List<Brand> getAll() {
        return brandService.getAllBrands();
    }

    // READ (one)
    @GetMapping("/{id}")
    public Brand getById(@PathVariable Long id) {
        return brandService.getBrandById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Brand update(@PathVariable Long id, @RequestBody Brand brand) {
        return brandService.updateBrand(id, brand);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        brandService.deleteBrand(id);
    }
}