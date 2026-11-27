package com.example.back_end.service;

import com.example.back_end.model.CustomerOrder;
import com.example.back_end.model.OrderItem;
import com.example.back_end.model.Product;
import com.example.back_end.model.ShopSelection;
import com.example.back_end.model.User;
import com.example.back_end.repository.CustomerOrderRepository;
import com.example.back_end.repository.OrderItemRepository;
import com.example.back_end.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CheckoutService {
    public record Request(UUID requestId, String customerName, String customerEmail, String phoneNumber,
                          String address, String city, String paymentMethod, List<ReviewedItem> reviewedItems) {}
    public record ReviewedItem(Long productId, Integer quantity, BigDecimal unitPrice) {}
    public record Item(String sku, String name, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}
    public record Order(Long id, String orderNumber, String status, BigDecimal totalAmount,
                        String customerName, String customerEmail, String phoneNumber, String address,
                        String paymentMethod, List<Item> items) {}

    private final CartService carts;
    private final CustomerOrderRepository orders;
    private final OrderItemRepository items;
    private final ProductRepository products;

    public CheckoutService(CartService carts, CustomerOrderRepository orders, OrderItemRepository items,
                           ProductRepository products) {
        this.carts = carts;
        this.orders = orders;
        this.items = items;
        this.products = products;
    }

    @Transactional
    public Order place(User user, Request request) {
        if (request.requestId() == null) throw new IllegalArgumentException("Checkout request ID is required");
        ShopSelection selection = carts.lockSelection(user.getId());
        String orderNumber = "WEB-" + user.getId() + "-" + request.requestId();
        var previous = orders.findByOrderNumberIgnoreCase(orderNumber);
        if (previous.isPresent()) return toViews(List.of(previous.get())).getFirst();

        Map<Long, Integer> quantities = selection.quantities();
        if (quantities.isEmpty()) {
            if (request.reviewedItems() != null && !request.reviewedItems().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Your bag is now empty");
            }
            throw new IllegalArgumentException("Your bag is empty");
        }
        List<Product> catalogue = products.lockAllById(quantities.keySet());
        checkReviewedItems(request.reviewedItems(), catalogue, quantities);
        // Older checkout clients can still submit their current server-side bag.
        quantities.keySet().retainAll(catalogue.stream().map(Product::getId).collect(Collectors.toSet()));
        if (quantities.isEmpty()) throw new IllegalArgumentException("Your bag is empty");

        CustomerOrder order = buildOrder(user, request, orderNumber);
        List<OrderItem> lines = catalogue.stream()
                .map(product -> reserve(order, product, quantities.get(product.getId())))
                .toList();
        order.setTotalAmount(lines.stream().map(OrderItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        orders.save(order);
        items.saveAll(lines);
        quantities.clear();
        return toView(order, lines);
    }

    @Transactional(readOnly = true)
    public List<Order> history(Long userId) {
        return toViews(orders.findAllByUserIdOrderByIdDesc(userId));
    }

    @Transactional(readOnly = true)
    public List<Order> allOrders() {
        return toViews(orders.findAll(Sort.by("id").descending()));
    }

    private CustomerOrder buildOrder(User user, Request request, String number) {
        String payment = required(request.paymentMethod(), "Payment method", 30);
        if (!payment.equals("Cash on delivery")) {
            throw new IllegalArgumentException("Choose cash on delivery");
        }
        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setOrderNumber(number);
        order.setStatus("Pending");
        order.setCustomerName(required(request.customerName(), "Name", 150));
        String email = required(request.customerEmail(), "Email", 255);
        if (!email.matches("[^\\s@]+@[^\\s@]+")) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
        order.setCustomerEmail(email);
        order.setPhoneNumber(required(request.phoneNumber(), "Phone number", 32));
        order.setAddress(required(request.address(), "Address", 170) + ", " + required(request.city(), "City", 80));
        order.setMapLink("");
        order.setPaymentMethod(payment);
        return order;
    }

    private void checkReviewedItems(List<ReviewedItem> reviewed, List<Product> catalogue, Map<Long, Integer> quantities) {
        if (reviewed == null) return;

        Map<Long, ReviewedItem> byProduct = new HashMap<>();
        for (ReviewedItem item : reviewed) {
            if (item == null || item.productId() == null || item.quantity() == null || item.unitPrice() == null
                    || byProduct.putIfAbsent(item.productId(), item) != null) {
                throw new IllegalArgumentException("Invalid checkout items");
            }
        }

        boolean matches = byProduct.size() == catalogue.size() && catalogue.stream().allMatch(product -> {
            ReviewedItem item = byProduct.get(product.getId());
            return item != null && item.quantity().equals(quantities.get(product.getId()))
                    && item.unitPrice().compareTo(product.getPrice()) == 0;
        });
        if (!matches) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Your bag changed. Review the updated items and total before placing your order.");
        }
    }

    private OrderItem reserve(CustomerOrder order, Product product, int quantity) {
        if (!CartService.available(product) || quantity > product.getStock()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Update your bag. Availability changed for " + product.getName());
        }
        product.setStock(product.getStock() - quantity);
        OrderItem item = new OrderItem();
        item.setCustomerOrder(order);
        item.setProduct(product);
        item.setProductSku(product.getSku());
        item.setProductName(product.getName());
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());
        item.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    private List<Order> toViews(List<CustomerOrder> results) {
        if (results.isEmpty()) return List.of();
        var lines = items.findAllByCustomerOrderIdInOrderById(results.stream().map(CustomerOrder::getId).toList());
        var byOrder = lines.stream().collect(Collectors.groupingBy(item -> item.getCustomerOrder().getId()));
        return results.stream().map(order -> toView(order, byOrder.getOrDefault(order.getId(), List.of()))).toList();
    }

    private Order toView(CustomerOrder order, List<OrderItem> lines) {
        return new Order(order.getId(), order.getOrderNumber(), order.getStatus(), order.getTotalAmount(),
                order.getCustomerName(), order.getCustomerEmail(), order.getPhoneNumber(), order.getAddress(),
                order.getPaymentMethod(), lines.stream().map(line -> new Item(line.getProductSku(),
                        line.getProductName(), line.getQuantity(), line.getUnitPrice(), line.getLineTotal())).toList());
    }

    private String required(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.trim().length() > maxLength) {
            throw new IllegalArgumentException(field + " is required and must be at most " + maxLength + " characters");
        }
        return value.trim();
    }
}
