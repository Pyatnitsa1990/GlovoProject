package com.example.glovoproject.service;

import com.example.glovoproject.exceptions.OrderException;
import com.example.glovoproject.model.Order;
import com.example.glovoproject.model.Product;
import com.example.glovoproject.repository.OrderRepository;
import com.example.glovoproject.utils.OrderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public Order getById(String id) throws OrderException {
        return orderRepository.getById(id)
                .orElseThrow(() -> new OrderException("Order by id:" + id + " not found"));
    }
    public List<Order> getAll(){
        return orderRepository.getAll();
    }


    public void update(String id, List<Product> products) throws OrderException {
        if (id == null) {
            throw new OrderException("You must set id order");
        }

        if (products == null || products.isEmpty()) {
            throw new OrderException("You can't create order without products");
        }

        Optional<Order> existingOrder = orderRepository.getById(id);
        if (existingOrder.isPresent()) {
            Order updateOrder = existingOrder.get();

            updateOrder.getProducts().addAll(products);
            updateOrder.setCost(OrderUtils.calculateOrderCost(updateOrder.getProducts()));

        }
    }

    public void create(Order order) throws OrderException {
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new OrderException("You can't create order without products");
        }

        orderRepository.add(OrderUtils.createOrder(order.getProducts()));
    }
}
