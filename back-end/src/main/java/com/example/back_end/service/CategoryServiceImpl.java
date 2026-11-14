package com.example.back_end.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back_end.repository.CategoryRepository;
import com.example.back_end.model.Category;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // create function
    @Override
    @Transactional
    public Category createCategory(Category category) {
        category.setId(null);
        category.setName(text(category.getName(), "Category name"));
        category.setSlug(text(category.getSlug(), "Category slug").toLowerCase(Locale.ROOT));
        category.setDes(text(category.getDes(), "Category des"));
       
        checkUnique(category.getName(), category.getSlug(),category.getDes(), null, null, null);
        return categoryRepository.save(category);
    }
    //

    // read function
    @Override 
    @Transactional
    public List<Category> getCategory() {
        return categoryRepository.findAllByOrderByNameAsc();
    }
    //

    private void checkUnique(String name, String slug,String des, String oldName, String
    oldSlug, String oldDes) {
        if (!name.equals(oldName) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("A Brand with " + name + " already exist");
        }
        if (!slug.equals(oldSlug) && categoryRepository.existsByNameIgnoreCase(slug)) {
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
