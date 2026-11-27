package com.example.back_end.controller;

import java.util.List;
import java.util.NoSuchElementException;

import com.example.back_end.config.ApiAccess;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.back_end.model.Product;
import com.example.back_end.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // create method
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    // get method to get all
    @GetMapping
    public List<Product> getAll(@RequestParam(defaultValue = "false") boolean manage, HttpServletRequest request) {
        return productService.getAllProducts().stream()
                .filter(product -> visible(product, manage, request))
                .toList();
    }

    // get method to get one
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean manage,
                           HttpServletRequest request) {
        Product product = productService.getProductById(id);
        if (!visible(product, manage, request)) throw new NoSuchElementException("Product was not found");
        return product;
    }

    // update method
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    // delete method
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
    private boolean visible(Product product, boolean manage, HttpServletRequest request) {
        return (manage && ApiAccess.isAdmin(ApiAccess.account(request)))
                || (product.getProductStatus() == Product.ProductStatus.ACTIVE && product.getCategory().isActive());
    }
}
