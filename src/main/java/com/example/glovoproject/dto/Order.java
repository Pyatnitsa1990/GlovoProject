package com.example.glovoproject.dto;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Order {
    private Long id;
    private LocalDateTime date;
    private Double cost;
    private List<Product> products;
}