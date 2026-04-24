package com.crypto.exchange.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderResponse {

    private Long orderId;
    private String symbol;
    private String side;

    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal remainingQuantity;

    private String status;
}