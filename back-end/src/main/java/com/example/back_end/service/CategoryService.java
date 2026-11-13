package com.example.back_end.service;

import com.example.back_end.model.Category;
import com.example.back_end.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // READ ALL
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // READ BY ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // UPDATE
    public Optional<Category> updateCategory(Long id, Category category) {

        return categoryRepository.findById(id)
                .map(existingCategory -> {

                    existingCategory.setName(category.getName());
                    existingCategory.setSlug(category.getSlug());
                    existingCategory.setDescription(category.getDescription());

                    return categoryRepository.save(existingCategory);
                });
    }

    // DELETE
    public boolean deleteCategory(Long id) {

        if (!categoryRepository.existsById(id)) {
            return false;
        }

        categoryRepository.deleteById(id);
        return true;
    }
}