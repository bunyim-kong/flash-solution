package com.example.back_end.controller;

import com.example.back_end.model.Category;
import com.example.back_end.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }


    @GetMapping
    public List<Category> getAll() {
        return categoryService.getCategory();
    }
}

