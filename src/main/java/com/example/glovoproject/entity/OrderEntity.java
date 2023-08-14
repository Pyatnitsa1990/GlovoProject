package com.example.glovoproject.entity;

import com.example.glovoproject.utils.CostUtils;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime date;
    private Double cost;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products = new ArrayList<>();

    public void addProduct(ProductEntity product) {
        products.add(product);
        product.setOrder(this);
    }

    public void removeProduct(ProductEntity product) {
        this.cost = CostUtils.round(this.cost - product.getCost(), 2);
        products.remove(product);
        product.setOrder(null);
    }
}
