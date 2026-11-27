package com.example.back_end.controller;

import java.util.List;
import com.example.back_end.config.ApiAccess;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.back_end.model.Category;
import com.example.back_end.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    // READ ALL
    @GetMapping
    public List<Category> getAll(@RequestParam(defaultValue = "false") boolean manage, HttpServletRequest request) {
        return categoryService.getAllCategories().stream()
                .filter(category -> category.isActive() || (manage && ApiAccess.isAdmin(ApiAccess.account(request))))
                .toList();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public Category getById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Category update(
            @PathVariable Long id,
            @RequestBody Category category) {

        return categoryService.updateCategory(id, category);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}