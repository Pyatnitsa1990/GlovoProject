package com.example.glovoproject.service;

import com.example.glovoproject.entity.OrderEntity;
import com.example.glovoproject.exceptions.OrderException;
import com.example.glovoproject.dto.Order;
import com.example.glovoproject.dto.Product;
import com.example.glovoproject.mapper.OrderMapper;
import com.example.glovoproject.mapper.ProductMapper;
import com.example.glovoproject.repository.OrderRepository;
import com.example.glovoproject.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.glovoproject.utils.CostUtils.calculateOrderCost;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    public Order findById(Long id) throws OrderException {
        Optional<OrderEntity> entity = orderRepository.findById(id);
        return entity.map(OrderMapper::mapToDto)
                .orElseThrow(() -> new OrderException(String.format("Order with id:%s not found", id)));
    }

    public List<Order> getAll() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::mapToDto)
                .toList();
    }


    public void update(Long id, List<Product> products) throws OrderException {
        if (id == null) {
            throw new OrderException("You must set id order");
        }

        if (products == null || products.isEmpty()) {
            throw new OrderException("You can't create order without products");
        }

        Optional<OrderEntity> orderEntity = orderRepository.findById(id);
        if (orderEntity.isPresent()) {
            OrderEntity order = orderEntity.get();
            var productEntities = products.stream()
                    .map(product -> ProductMapper.mapToEntity(product, order))
                    .toList();
            productRepository.saveAll(productEntities);
            order.setCost(calculateOrderCost(order.getProducts()));
            orderRepository.save(order);
        } else {
            throw new OrderException(String.format("Order with id:%s doesn't exists", id));
        }

    }

    public void save(Order order) throws OrderException {
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new OrderException("You can't create order without products");
        }

        OrderEntity orderEntity = OrderMapper.mapToEntity(order);
        orderEntity.setCost(calculateOrderCost(orderEntity.getProducts()));
        orderRepository.save(orderEntity);
    }

    public void delete(Long id) {
        Optional<OrderEntity> order = orderRepository.findById(id);
        order.ifPresent(orderRepository::delete);
    }

    public void deleteProductFromOrder(Long orderId, Long productId) {
        var orderEntity = orderRepository.findById(orderId);

        if (orderEntity.isPresent()) {//check if order exists
            var productEntity = productRepository.findById(productId);

            if (productEntity.isPresent()) {//check if product exists
                OrderEntity entity = orderEntity.get();
                entity.removeProduct(productEntity.get());
                orderRepository.save(entity);
            }
        }
    }
}