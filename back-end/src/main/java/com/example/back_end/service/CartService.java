package com.example.back_end.service;

import com.example.back_end.model.Product;
import com.example.back_end.model.ShopSelection;
import com.example.back_end.model.User;
import com.example.back_end.repository.ProductRepository;
import com.example.back_end.repository.ShopSelectionRepository;
import com.example.back_end.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

@Service
public class CartService {
    public static final String GUEST_SELECTION = "guestSelection";

    public record Line(Product product, int quantity) {}
    public record View(List<Line> items, List<Long> savedProductIds) {}

    private final ShopSelectionRepository selections;
    private final UserRepository users;
    private final ProductRepository products;

    public CartService(ShopSelectionRepository selections, UserRepository users, ProductRepository products) {
        this.selections = selections;
        this.users = users;
        this.products = products;
    }

    @Transactional(readOnly = true)
    public View get(Long userId, HttpSession session) {
        if (userId == null) {
            if (session == null) return new View(List.of(), List.of());
            synchronized (session) {
                return view(guest(session));
            }
        }
        return view(selections.findById(userId).orElseGet(() -> new ShopSelection(userId)));
    }

    @Transactional
    public View setQuantity(Long userId, HttpSession session, Long productId, int quantity, boolean increment) {
        return change(userId, session, selection -> {
            long requested = increment ? (long) selection.quantities().getOrDefault(productId, 0) + 1 : quantity;
            if (requested == 0) {
                selection.quantities().remove(productId);
                return;
            }
            Product product = requireAvailable(productId);
            if (requested < 0 || requested > product.getStock()) {
                throw new IllegalArgumentException("Only " + product.getStock() + " available for " + product.getName());
            }
            selection.quantities().put(productId, (int) requested);
        });
    }

    @Transactional
    public View saveProduct(Long userId, HttpSession session, Long productId, boolean saved) {
        return change(userId, session, selection -> {
            if (saved) {
                requireAvailable(productId);
                selection.saved().add(productId);
            } else {
                selection.saved().remove(productId);
            }
        });
    }

    @Transactional
    public void mergeGuest(Long userId, HttpSession session) {
        if (session == null || session.getAttribute(GUEST_SELECTION) == null) return;
        ShopSelection guest = guest(session);
        ShopSelection account = lockSelection(userId);
        for (Product product : products.findAllById(guest.quantities().keySet())) {
            if (available(product) && product.getStock() > 0) {
                int quantity = guest.quantities().get(product.getId());
                account.quantities().merge(product.getId(), Math.min(quantity, product.getStock()),
                        (existing, added) -> (int) Math.min(product.getStock(), (long) existing + added));
            }
        }
        account.saved().addAll(guest.saved());
        selections.save(account);
    }

    ShopSelection lockSelection(Long userId) {
        users.lockById(userId).filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to continue"));
        return selections.findById(userId).orElseGet(() -> new ShopSelection(userId));
    }

    private View change(Long userId, HttpSession session, Consumer<ShopSelection> update) {
        if (userId == null) {
            synchronized (session) {
                ShopSelection selection = guest(session);
                update.accept(selection);
                return view(selection);
            }
        }
        ShopSelection selection = lockSelection(userId);
        update.accept(selection);
        return view(selections.save(selection));
    }

    private ShopSelection guest(HttpSession session) {
        if (session.getAttribute(GUEST_SELECTION) instanceof ShopSelection selection) return selection;
        ShopSelection selection = new ShopSelection(null);
        session.setAttribute(GUEST_SELECTION, selection);
        return selection;
    }

    private View view(ShopSelection selection) {
        List<Line> items = products.findAllById(selection.quantities().keySet()).stream()
                .sorted(Comparator.comparing(Product::getId))
                .map(product -> new Line(product, selection.quantities().get(product.getId())))
                .toList();
        return new View(items, List.copyOf(selection.saved()));
    }

    static boolean available(Product product) {
        return product.getProductStatus() == Product.ProductStatus.ACTIVE && product.getCategory().isActive();
    }

    private Product requireAvailable(Long id) {
        return products.findById(id).filter(CartService::available)
                .orElseThrow(() -> new NoSuchElementException("Product is no longer available"));
    }
}
