package com.example.back_end.service;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back_end.model.Category;
import com.example.back_end.repository.CategoryRepository;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    @Override
    @Transactional
    public Category createCategory(Category category) {

        category.setId(null);

        category.setName(text(category.getName(), "Category name"));

        category.setSlug(
            text(category.getSlug(), "Category slug")
                .toLowerCase(Locale.ROOT)
        );

        checkUnique(category.getName(), category.getSlug(), null, null);

        return categoryRepository.save(category);
    }

    // READ ALL
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    // READ BY ID
    @Override
    public Category getCategoryById(Long id) {

        return categoryRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException(
                "Category with ID " + id + " was not found"
            )
        );
    }

    // UPDATE
    @Override
    @Transactional
    public Category updateCategory(Long id, Category category) {

        Category existing = getCategoryById(id);

        String name = text(category.getName(), "Category name");

        String slug = text(category.getSlug(), "Category slug")
            .toLowerCase(Locale.ROOT);

        checkUnique(name, slug, existing.getName(), existing.getSlug());

        existing.setName(name);
        existing.setSlug(slug);
        existing.setDescription(category.getDescription());
        existing.setActive(category.isActive());

        return existing;
    }

    // DELETE
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.delete(getCategoryById(id));
    }

    // CHECK UNIQUE NAME AND SLUG
    private void checkUnique(
        String name,
        String slug,
        String oldName,
        String oldSlug
    ) {

        if (!name.equals(oldName)
                && categoryRepository.existsByNameIgnoreCase(name)) {

            throw new IllegalArgumentException(
                "A Category with " + name + " already exists"
            );
        }

        if (!slug.equals(oldSlug)
                && categoryRepository.existsBySlugIgnoreCase(slug)) {

            throw new IllegalArgumentException(
                "A Category with " + slug + " already exists"
            );
        }
    }

    // VALIDATE TEXT
    private String text(String value, String field) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                field + " is required."
            );
        }

        return value.trim();
    }
}