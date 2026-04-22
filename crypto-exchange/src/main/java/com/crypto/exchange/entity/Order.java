package com.crypto.exchange.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String symbol;
    private String side;

    private BigDecimal price;
    private BigDecimal quantity;

    private BigDecimal remainingQuantity;

    private String status;
}