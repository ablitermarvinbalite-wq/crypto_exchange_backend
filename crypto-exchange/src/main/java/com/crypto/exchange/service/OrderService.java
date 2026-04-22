package com.crypto.exchange.service;

import com.crypto.exchange.dto.OrderRequest;
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

        return orderRepository.save(order);
    }

    private void validate(OrderRequest request) {

        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0 ||
                request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid price or quantity");
        }
    }
}
