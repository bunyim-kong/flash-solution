package com.example.back_end;

import com.example.back_end.model.Brand;
import com.example.back_end.model.Category;
import com.example.back_end.model.Product;
import com.example.back_end.service.BrandService;
import com.example.back_end.service.CategoryService;
import com.example.back_end.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class StoreIntegrationTests {
    @Autowired private WebApplicationContext context;
    @Autowired private BrandService brands;
    @Autowired private CategoryService categories;
    @Autowired private ProductService products;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void guestCheckoutRequiresTheReviewedPriceAndRetriesOnlyReserveStockOnce() throws Exception {
        Long productId = sampleProduct().getId();
        MockHttpSession session = new MockHttpSession();
        mvc.perform(post("/api/cart/items/{id}", productId).session(session).header("X-Lumiere-Request", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].quantity").value(1));
        register(session, "Checkout Customer", "checkout@example.test")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        mvc.perform(get("/api/cart").session(session)).andExpect(jsonPath("$.items[0].product.id").value(productId));

        String checkout = """
                {"requestId":"%s","customerName":"Checkout Customer","customerEmail":"checkout@example.test",
                 "phoneNumber":"+8551234567","address":"1 Test Street","city":"Phnom Penh",
                 "paymentMethod":"Cash on delivery","totalAmount":0.01,
                 "reviewedItems":[{"productId":%d,"quantity":1,"unitPrice":12.50}]}
                """.formatted(UUID.randomUUID(), productId);

        Product changed = products.getProductById(productId);
        changed.setPrice(new BigDecimal("15.00"));
        products.updateProduct(productId, changed);
        order(session, checkout).andExpect(status().isConflict());
        mvc.perform(get("/api/products/{id}", productId)).andExpect(jsonPath("$.stock").value(3));
        mvc.perform(get("/api/cart").session(session)).andExpect(jsonPath("$.items[0].quantity").value(1));
        mvc.perform(get("/api/orders/my").session(session)).andExpect(jsonPath("$").isEmpty());

        checkout = checkout.replace("12.50", "15.00");
        order(session, checkout).andExpect(status().isCreated()).andExpect(jsonPath("$.totalAmount").value(15));
        order(session, checkout).andExpect(status().isCreated()).andExpect(jsonPath("$.items[0].quantity").value(1));

        mvc.perform(get("/api/products/{id}", productId)).andExpect(jsonPath("$.stock").value(2));
        mvc.perform(get("/api/cart").session(session)).andExpect(jsonPath("$.items").isEmpty());
        mvc.perform(get("/api/orders/my").session(session))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].name").value("Checkout Serum"));
    }

    @Test
    void registrationCannotGrantAdminAndWritesRequireTheRequestHeader() throws Exception {
        MockHttpSession session = new MockHttpSession();
        register(session, "Access Customer", "access@example.test")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("CUSTOMER"));
        mvc.perform(get("/api/admin/orders").session(session)).andExpect(status().isForbidden());
        mvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/logout").session(session)).andExpect(status().isForbidden());
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isOk());
        mvc.perform(get("/api/orders/my").session(session)).andExpect(jsonPath("$").isEmpty());
    }

    private ResultActions register(MockHttpSession session, String name, String email) throws Exception {
        return mvc.perform(json(post("/api/auth/register"), session, """
                {"name":"%s","email":"%s","password":"test-password","role":"ADMIN"}
                """.formatted(name, email)));
    }

    private ResultActions order(MockHttpSession session, String body) throws Exception {
        return mvc.perform(json(post("/api/orders"), session, body));
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder builder, MockHttpSession session, String body) {
        return builder.session(session).header("X-Lumiere-Request", "1").contentType("application/json").content(body);
    }

    private Product sampleProduct() {
        Brand brand = new Brand();
        brand.setName("Checkout Beauty");
        brand.setSlug("checkout-beauty");

        Category category = new Category();
        category.setName("Checkout skincare");
        category.setSlug("checkout-skincare");

        Product product = new Product();
        product.setName("Checkout Serum");
        product.setSku("TEST-CHECKOUT");
        product.setBrand(brands.createBrand(brand));
        product.setCategory(categories.createCategory(category));
        product.setProductStatus(Product.ProductStatus.ACTIVE);
        product.setPrice(new BigDecimal("12.50"));
        product.setStock(3);
        return products.createProduct(product);
    }
}
