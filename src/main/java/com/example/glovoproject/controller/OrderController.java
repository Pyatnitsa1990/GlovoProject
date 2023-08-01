package com.example.glovoproject.controller;

import com.example.glovoproject.exceptions.OrderException;
import com.example.glovoproject.model.Order;
import com.example.glovoproject.model.Product;
import com.example.glovoproject.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    private OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(orderService.getById(id));
        } catch (OrderException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody List<Product> products) {
        try {
            orderService.update(id, products);
            return ResponseEntity
                    .ok("Order with id:" + id + " was updated");
        } catch (OrderException ex) {
            return ResponseEntity.badRequest()
                    .body(ex.getLocalizedMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Order order) {
        try {
            orderService.create(order);
            return ResponseEntity
                    .status(HttpStatus.CREATED).build();
        } catch (OrderException ex) {
            return ResponseEntity.badRequest()
                    .body(ex.getLocalizedMessage());
        }
    }

    @GetMapping
    public List<Order> getAll() {
        return orderService.getAll();
    }

}
