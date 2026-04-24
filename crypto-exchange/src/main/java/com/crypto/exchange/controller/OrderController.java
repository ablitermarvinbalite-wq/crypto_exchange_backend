package com.crypto.exchange.controller;

import com.crypto.exchange.dto.OrderRequest;
import com.crypto.exchange.dto.OrderResponse;
import com.crypto.exchange.entity.Order;
import com.crypto.exchange.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order place(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @DeleteMapping("/{orderId}")
    public String cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return "Order cancelled";
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }
}
