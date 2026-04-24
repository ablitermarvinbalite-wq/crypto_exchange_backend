package com.crypto.exchange.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderBookResponse {

    private List<Level> bids;
    private List<Level> asks;

    @Data
    @Builder
    public static class Level {
        private BigDecimal price;
        private BigDecimal quantity;
    }
}
