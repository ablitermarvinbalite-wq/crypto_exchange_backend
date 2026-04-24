package com.crypto.exchange.service;

import com.crypto.exchange.dto.OrderBookResponse;
import com.crypto.exchange.engine.MatchingEngine;
import com.crypto.exchange.engine.OrderBook;
import com.crypto.exchange.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderBookService {

    private final MatchingEngine matchingEngine;

    public OrderBookResponse getOrderBook(String symbol) {

        OrderBook orderBook = matchingEngine.getOrderBook(symbol);

        List<OrderBookResponse.Level> bids =
                orderBook.getBuyOrders().stream()
                        .map(this::map)
                        .collect(Collectors.toList());

        List<OrderBookResponse.Level> asks =
                orderBook.getSellOrders().stream()
                        .map(this::map)
                        .collect(Collectors.toList());

        return OrderBookResponse.builder()
                .bids(bids)
                .asks(asks)
                .build();
    }

    private OrderBookResponse.Level map(Order order) {
        return OrderBookResponse.Level.builder()
                .price(order.getPrice())
                .quantity(order.getRemainingQuantity())
                .build();
    }
}