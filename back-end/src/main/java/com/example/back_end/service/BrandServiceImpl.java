package com.example.back_end.service;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back_end.model.Brand;
import com.example.back_end.repository.BrandRepository;

@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    // funcation create
    @Override
    @Transactional  
    public Brand createBrand(Brand brand) {
        brand.setId(null);
        brand.setName(text(brand.getName(), "Brand name"));
        brand.setSlug(text(brand.getSlug(), "Brand slug"). toLowerCase(Locale.ROOT));

        checkUnique(brand.getName(), brand.getSlug(), null, null);
        return brandRepository.save(brand);
    }
    //


    // function read
    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("Brand with Id " + id + " was not found"));
    }
    //


    // function update
    @Override
    @Transactional
    public Brand updateBrand(Long id, Brand brand) {
        Brand existing = getBrandById(id);
        String name = text(brand.getName(), "Brand name");
        String slug = text(brand.getSlug(), "Brand slug").toLowerCase(Locale.ROOT);

        checkUnique(name, slug, existing.getName(), existing.getSlug());

        existing.setName(name);
        existing.setSlug(slug);
        return existing;   
    }
    //


    // function delete
    @Override
    @Transactional
    public void deleteBrand(Long id) {
        brandRepository.delete(getBrandById(id));
    }
    //


    private void checkUnique(String name, String slug, String oldName, String oldSlug) {
        if (!name.equals(oldName) && brandRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("A Brand with " + name + " already exist");
        }

        if (!slug.equals(oldSlug) && brandRepository.existsBySlugIgnoreCase(slug)) {
            throw new IllegalArgumentException("A Brand with " + slug +" already exist");
        }
    }

    private String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is require.");
        }
        return value.trim();
    }
}
