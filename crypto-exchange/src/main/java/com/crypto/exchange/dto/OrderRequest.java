package com.crypto.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {

    private Long userId;
    private String symbol;
    private String side;

    private BigDecimal price;
    private BigDecimal quantity;
}
