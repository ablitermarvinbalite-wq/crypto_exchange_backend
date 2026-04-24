package com.crypto.exchange.service;

import com.crypto.exchange.dto.OrderRequest;
import com.crypto.exchange.dto.OrderResponse;
import com.crypto.exchange.engine.MatchingEngine;
import com.crypto.exchange.entity.Order;
import com.crypto.exchange.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final MatchingEngine matchingEngine;

    private static final List<String> QUOTES = List.of("USDT", "USDC", "BTC");

    @Transactional
    public Order placeOrder(OrderRequest request) {

        validate(request);

        String[] parts = parseSymbol(request.getSymbol());
        String base = parts[0];
        String quote = parts[1];

        // LOCK FUNDS
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

        // CREATE ORDER
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

        String[] parts = parseSymbol(order.getSymbol());
        String base = parts[0];
        String quote = parts[1];

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

    public List<OrderResponse> getUserOrders(Long userId) {

        return orderRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    private OrderResponse map(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .symbol(order.getSymbol())
                .side(order.getSide())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .remainingQuantity(order.getRemainingQuantity())
                .status(order.getStatus())
                .build();
    }

    private String[] parseSymbol(String symbol) {

        for (String quote : QUOTES) {
            if (symbol.endsWith(quote)) {
                String base = symbol.substring(0, symbol.length() - quote.length());
                return new String[]{base, quote};
            }
        }

        throw new RuntimeException("Invalid symbol: " + symbol);
    }
}
