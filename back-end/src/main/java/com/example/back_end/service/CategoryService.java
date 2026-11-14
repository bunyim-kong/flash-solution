package com.example.back_end.service;

import java.util.List;
import com.example.back_end.model.Category;

public interface CategoryService {
    Category createCategory(Category category);
    List<Category> getCategory();
}
