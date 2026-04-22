package com.crypto.exchange.controller;

import com.crypto.exchange.dto.OrderRequest;
import com.crypto.exchange.entity.Order;
import com.crypto.exchange.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order place(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }
}
