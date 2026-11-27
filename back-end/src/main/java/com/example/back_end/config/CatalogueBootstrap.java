package com.example.back_end.config;

import com.example.back_end.model.Brand;
import com.example.back_end.model.Category;
import com.example.back_end.model.Product;
import com.example.back_end.model.Product.ProductStatus;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.service.BrandService;
import com.example.back_end.service.CategoryService;
import com.example.back_end.service.ProductService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "lumiere.seed-catalogue", havingValue = "true")
public class CatalogueBootstrap implements ApplicationRunner {
    private final ObjectMapper mapper;
    private final ProductRepository products;
    private final ProductService productService;
    private final BrandService brands;
    private final CategoryService categories;

    public CatalogueBootstrap(ObjectMapper mapper, ProductRepository products, ProductService productService,
                              BrandService brands, CategoryService categories) {
        this.mapper = mapper;
        this.products = products;
        this.productService = productService;
        this.brands = brands;
        this.categories = categories;
    }

    private record Sample(String sku, String name, String brand, String category, BigDecimal price, int stock,
                          ProductStatus productStatus, boolean featured, String imageUrl, String description) {}

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (products.count() > 0) return;
        Map<String, Brand> brandByName = new HashMap<>();
        brands.getAllBrands().forEach(brand -> brandByName.put(brand.getName().toLowerCase(Locale.ROOT), brand));
        Map<String, Category> categoryByName = new HashMap<>();
        categories.getAllCategories().forEach(category -> categoryByName.put(category.getName().toLowerCase(Locale.ROOT), category));

        try (var file = new ClassPathResource("catalogue/sample-products.json").getInputStream()) {
            for (Sample sample : mapper.readValue(file, Sample[].class)) {
                Product product = new Product();
                product.setSku(sample.sku());
                product.setName(sample.name());
                product.setBrand(brandByName.computeIfAbsent(sample.brand().toLowerCase(Locale.ROOT), key -> createBrand(sample.brand())));
                product.setCategory(categoryByName.computeIfAbsent(sample.category().toLowerCase(Locale.ROOT), key -> createCategory(sample.category())));
                product.setPrice(sample.price());
                product.setStock(sample.stock());
                product.setProductStatus(sample.productStatus());
                product.setFeatured(sample.featured());
                product.setImageUrl(sample.imageUrl());
                product.setDescription(sample.description());
                productService.createProduct(product);
            }
        }
    }

    private Brand createBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(slug(name));
        return brands.createBrand(brand);
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug(name));
        return categories.createCategory(category);
    }

    private String slug(String name) {
        return name.toLowerCase(Locale.ROOT).replace(' ', '-');
    }
}
