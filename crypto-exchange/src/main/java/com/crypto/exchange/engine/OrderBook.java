package com.crypto.exchange.engine;

import com.crypto.exchange.entity.Order;
import lombok.Getter;

import java.util.Comparator;
import java.util.PriorityQueue;

@Getter
public class OrderBook {

    // Highest BUY first
    private final PriorityQueue<Order> buyOrders =
            new PriorityQueue<>(Comparator.comparing(Order::getPrice).thenComparing(Order::getCreatedAt).reversed());

    // Lowest SELL first
    private final PriorityQueue<Order> sellOrders =
            new PriorityQueue<>(Comparator.comparing(Order::getPrice).thenComparing(Order::getCreatedAt));

}