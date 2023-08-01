package com.example.glovoproject.repository;

import com.example.glovoproject.model.Order;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.example.glovoproject.utils.OrderUtils.createOrder;

@Repository
public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    @PostConstruct
    public void init() {
        IntStream.range(1, 10)
                .forEach(i -> orders.add(createOrder(null)));

    }

    public Optional<Order> getById(String id){
        return orders.stream().filter(order -> id.equals(order.getId())).findFirst();
    }

    public List<Order> getAll() {
        return new ArrayList<>(orders);
    }

    public void add(Order order) {
        orders.add(order);
    }

}
