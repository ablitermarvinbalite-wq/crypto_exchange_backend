package com.crypto.exchange.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "trades")
@Getter
@Setter
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long buyOrderId;
    private Long sellOrderId;

    private Long buyerId;
    private Long sellerId;

    private String symbol;

    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal total;
}