package com.crypto.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletRequest {

    private Long userId;
    private String asset;
    private BigDecimal amount;
}
