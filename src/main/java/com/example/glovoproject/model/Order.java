package com.example.glovoproject.model;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Order {
    private String id;
    private LocalDateTime date;
    private Double cost;
    private List<Product> products;
}
