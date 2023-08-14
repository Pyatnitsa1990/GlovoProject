package com.example.glovoproject.utils;

import com.example.glovoproject.entity.ProductEntity;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@UtilityClass
public class CostUtils {
    public static double round(double value, int scale) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static Double calculateOrderCost(List<ProductEntity> products) {
        if (products == null || products.isEmpty())
            return null;

        Double orderCost = 0d;
        for (ProductEntity product : products) {
            orderCost += product.getCost();
        }
        return orderCost;
    }
}
