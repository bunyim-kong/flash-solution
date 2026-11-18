package com.example.back_end.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.back_end.model.CustomerOrder;
import com.example.back_end.model.OrderItem;
import com.example.back_end.model.Product;
import com.example.back_end.repository.CustomerOrderRepository;
import com.example.back_end.repository.OrderItemRepository;
import com.example.back_end.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTests {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CustomerOrderRepository customerOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemServiceImpl service;

    @Test
    void createUsesTrustedProductSnapshotAndPrice() {
        CustomerOrder storedOrder = customerOrder(10L);
        Product storedProduct = product(20L, "SKU-20", "Trusted product", "12.50");

        OrderItem request = new OrderItem();
        request.setCustomerOrder(customerOrder(10L));
        request.setProduct(product(20L, "FAKE", "Fake product", "0.01"));
        request.setQuantity(3);
        request.setProductSku("FAKE");
        request.setProductName("Fake product");
        request.setUnitPrice(new BigDecimal("0.01"));

        when(customerOrderRepository.findById(10L)).thenReturn(Optional.of(storedOrder));
        when(productRepository.findById(20L)).thenReturn(Optional.of(storedProduct));
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderItem saved = service.createOrderItem(request);

        assertSame(storedOrder, saved.getCustomerOrder());
        assertSame(storedProduct, saved.getProduct());
        assertEquals("SKU-20", saved.getProductSku());
        assertEquals("Trusted product", saved.getProductName());
        assertEquals(new BigDecimal("12.50"), saved.getUnitPrice());
        assertEquals(new BigDecimal("37.50"), saved.getLineTotal());
    }

    @Test
    void createRejectsNonPositiveQuantity() {
        OrderItem request = new OrderItem();
        request.setCustomerOrder(customerOrder(10L));
        request.setProduct(product(20L, "SKU-20", "Product", "12.50"));
        request.setQuantity(0);

        when(customerOrderRepository.findById(10L))
                .thenReturn(Optional.of(customerOrder(10L)));
        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product(20L, "SKU-20", "Product", "12.50")));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createOrderItem(request)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    private CustomerOrder customerOrder(Long id) {
        CustomerOrder order = new CustomerOrder();
        order.setId(id);
        return order;
    }

    private Product product(Long id, String sku, String name, String price) {
        Product product = new Product();
        product.setId(id);
        product.setSku(sku);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        return product;
    }
}
