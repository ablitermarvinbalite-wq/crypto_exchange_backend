package com.crypto.exchange.controller;

import com.crypto.exchange.dto.OrderBookResponse;
import com.crypto.exchange.service.OrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orderbook")
@RequiredArgsConstructor
public class OrderBookController {

    private final OrderBookService orderBookService;

    @GetMapping("/{symbol}")
    public OrderBookResponse get(@PathVariable String symbol) {
        return orderBookService.getOrderBook(symbol);
    }
}
