package com.example.glovoproject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private Long id;
    private String name;
    private Double cost;
}
