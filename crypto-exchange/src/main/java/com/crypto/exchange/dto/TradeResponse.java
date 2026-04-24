package com.crypto.exchange.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TradeResponse {

    private Long tradeId;

    private String symbol;

    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal total;

    private String side; // BUY or SELL (from user perspective)

    private Long counterpartyId;
}
