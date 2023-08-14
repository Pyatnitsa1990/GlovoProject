package com.example.glovoproject.mapper;

import com.example.glovoproject.entity.OrderEntity;
import com.example.glovoproject.dto.Order;
import com.example.glovoproject.entity.ProductEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;


@UtilityClass
public class OrderMapper {
    public static Order mapToDto(OrderEntity entity) {
        var products = entity.getProducts().stream()
                .map(ProductMapper::mapToDto)
                .toList();

        return Order.builder()
                .id(entity.getId())
                .cost(entity.getCost())
                .date(entity.getDate())
                .products(products)
                .build();
    }

    public static OrderEntity mapToEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        List<ProductEntity> products = order.getProducts().stream()
                .map(ProductMapper::mapToEntity)
                .toList();

        entity.setDate(LocalDateTime.now());
        entity.setCost(order.getCost());
        products.forEach(entity::addProduct);

        return entity;
    }
}
