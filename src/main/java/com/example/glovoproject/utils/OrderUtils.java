package com.example.glovoproject.utils;

import com.example.glovoproject.model.Order;
import com.example.glovoproject.model.Product;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class OrderUtils {

    public static Order createOrder(List<Product> products) {


        return Order.builder()
                .id(UUID.randomUUID().toString())
                .date(LocalDateTime.now())
                .cost(calculateOrderCost(products))
                .products(products)
                .build();
    }

    public static Double calculateOrderCost(List<Product> products) {
        if (products == null || products.isEmpty())
            return null;

        Double orderCost = 0d;
        for (Product product : products) {
            orderCost += product.getCost();
        }
        return orderCost;
    }

}
