package com.example.back_end.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class ShopSelection implements Serializable {
    @Id
    private Long userId;

    @ElementCollection
    @CollectionTable(name = "cart_entries", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    private Map<Long, Integer> quantities = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "saved_products", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "product_id", nullable = false)
    private Set<Long> saved = new HashSet<>();

    protected ShopSelection() {}

    public ShopSelection(Long userId) {
        this.userId = userId;
    }

    public Map<Long, Integer> quantities() {
        return quantities;
    }

    public Set<Long> saved() {
        return saved;
    }
}
