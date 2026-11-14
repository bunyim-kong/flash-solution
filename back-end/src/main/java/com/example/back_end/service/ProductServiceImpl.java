package com.example.back_end.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back_end.model.Product;
import com.example.back_end.model.Product.ProductStatus;
import com.example.back_end.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // function create
    @Override
    @Transactional
    public Product createProduct(Product product) {
        product.setId(null);
        product.setSku(text(product.getSku(), "Product sku").toUpperCase(Locale.ROOT));
        product.setName(text(product.getName(), "Product name"));
        product.setBrand(required(product.getBrand(), "Product brand"));
        product.setCategory(required(product.getCategory(), "Product category"));
        product.setPrice(nonNegative(product.getPrice(), "Product price"));
        product.setStock(nonNegative(product.getStock(), "Product stock"));
        product.setProductStatus(product.getProductStatus() == null ? ProductStatus.DRAFT : product.getProductStatus());
        product.setDescription(optionalText(product.getDescription()));
        product.setImageUrl(optionalText(product.getImageUrl()));

        checkUnique(product.getSku(), null);
        return productRepository.save(product);
    }

    // function read
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("Product with Id " + id + " was not found."));
    }

    // function update
    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existing = getProductById(id);
        String sku = text(product.getSku(), "Product sku").toUpperCase(Locale.ROOT);

        checkUnique(sku, existing.getSku());

        existing.setSku(sku);
        existing.setName(text(product.getName(), "Product name"));
        existing.setBrand(required(product.getBrand(), "Product brand"));
        existing.setCategory(required(product.getCategory(), "Product category"));
        existing.setPrice(nonNegative(product.getPrice(), "Product price"));
        existing.setDescription(optionalText(product.getDescription()));
        existing.setImageUrl(optionalText(product.getImageUrl()));
        existing.setStock(nonNegative(product.getStock(), "Product stock"));
        existing.setProductStatus(product.getProductStatus() == null ? ProductStatus.DRAFT : product.getProductStatus());
        existing.setFeatured(product.isFeatured());
        return existing;
    }

    // function delete
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.delete(getProductById(id));
    }

    private void checkUnique(String sku, String oldSku) {
        if (!sku.equals(oldSku) && productRepository.existsBySkuIgnoreCase(sku)) {
            throw new IllegalArgumentException("A Product with " + sku + " already exist.");
        }
    }

    private String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is require.");
        }
        return value.trim();
    }

    private String optionalText(String value) {
        return value == null ? null : value.trim();
    }

    private <T> T required(T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is require.");
        }
        return value;
    }

    private BigDecimal nonNegative(BigDecimal value, String field) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(field + " must be zero or greater.");
        }
        return value;
    }

    private Integer nonNegative(Integer value, String field) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException(field + " must be zero or greater.");
        }
        return value;
    }
}
