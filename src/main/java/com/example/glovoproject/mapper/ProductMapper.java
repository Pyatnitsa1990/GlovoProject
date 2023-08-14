package com.example.glovoproject.mapper;

import com.example.glovoproject.entity.OrderEntity;
import com.example.glovoproject.entity.ProductEntity;
import com.example.glovoproject.dto.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

    public static Product mapToDto(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .cost(entity.getCost())
                .build();
    }

    public static ProductEntity mapToEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setName(product.getName());
        entity.setCost(product.getCost());
        return entity;
    }

    public static ProductEntity mapToEntity(Product product, OrderEntity orderEntity) {
        ProductEntity entity = new ProductEntity();
        entity.setName(product.getName());
        entity.setCost(product.getCost());
        entity.setOrder(orderEntity);
        return entity;
    }
}
