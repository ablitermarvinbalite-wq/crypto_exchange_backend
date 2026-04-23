package com.crypto.exchange.service;

import com.crypto.exchange.dto.OrderRequest;
import com.crypto.exchange.engine.MatchingEngine;
import com.crypto.exchange.entity.Order;
import com.crypto.exchange.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final MatchingEngine matchingEngine;

    @Transactional
    public Order placeOrder(OrderRequest request) {

        validate(request);

        String base = "BTC";
        String quote = "USDT";

        // 🔒 LOCK FUNDS
        if ("BUY".equalsIgnoreCase(request.getSide())) {

            BigDecimal total = request.getPrice()
                    .multiply(request.getQuantity());

            walletService.lockBalance(request.getUserId(), quote, total);

        } else {

            walletService.lockBalance(
                    request.getUserId(),
                    base,
                    request.getQuantity()
            );
        }

        // 📊 CREATE ORDER
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setSymbol(request.getSymbol());
        order.setSide(request.getSide());

        order.setPrice(request.getPrice());
        order.setQuantity(request.getQuantity());
        order.setRemainingQuantity(request.getQuantity());

        order.setStatus("OPEN");

        Order saved = orderRepository.save(order);

        matchingEngine.process(saved);

        return saved;
    }

    private void validate(OrderRequest request) {

        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0 ||
                request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid price or quantity");
        }
    }

    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("FILLED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel filled order");
        }

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Order already cancelled");
        }

        BigDecimal remaining = order.getRemainingQuantity();

        String base = "BTC";
        String quote = "USDT";

        // 🔓 UNLOCK FUNDS
        if ("BUY".equalsIgnoreCase(order.getSide())) {

            BigDecimal refund = order.getPrice().multiply(remaining);

            walletService.unlockBalance(
                    order.getUserId(),
                    quote,
                    refund
            );

        } else {

            walletService.unlockBalance(
                    order.getUserId(),
                    base,
                    remaining
            );
        }

        order.setStatus("CANCELLED");

        orderRepository.save(order);
    }
}
