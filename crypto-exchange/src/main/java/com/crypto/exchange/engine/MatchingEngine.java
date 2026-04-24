package com.crypto.exchange.engine;

import com.crypto.exchange.entity.Order;
import com.crypto.exchange.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class MatchingEngine {

    private final Map<String, OrderBook> orderBooks = new ConcurrentHashMap<>();
    private final TradeService tradeService;

    public void process(Order order) {

        OrderBook orderBook = getOrderBook(order.getSymbol());

        if ("BUY".equalsIgnoreCase(order.getSide())) {
            matchBuy(order, orderBook);
        } else {
            matchSell(order, orderBook);
        }
    }

    private void matchBuy(Order buy, OrderBook orderBook) {

        while (!orderBook.getSellOrders().isEmpty()) {

            Order sell = orderBook.getSellOrders().peek();

            if (buy.getPrice().compareTo(sell.getPrice()) < 0) break;

            executeTrade(buy, sell);

            if (sell.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                orderBook.getSellOrders().poll();
            }

            if (buy.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
        }

        orderBook.getBuyOrders().add(buy);
    }

    private void matchSell(Order sell, OrderBook orderBook) {

        while (!orderBook.getBuyOrders().isEmpty()) {

            Order buy = orderBook.getBuyOrders().peek();

            if (buy.getPrice().compareTo(sell.getPrice()) < 0) break;

            executeTrade(buy, sell);

            if (buy.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                orderBook.getBuyOrders().poll();
            }

            if (sell.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
        }

        orderBook.getSellOrders().add(sell);
    }

    private void executeTrade(Order buy, Order sell) {

        BigDecimal tradeQty = buy.getRemainingQuantity()
                .min(sell.getRemainingQuantity());

        BigDecimal tradePrice = sell.getPrice();

        tradeService.executeTrade(buy, sell, tradeQty, tradePrice);

        buy.setRemainingQuantity(buy.getRemainingQuantity().subtract(tradeQty));
        sell.setRemainingQuantity(sell.getRemainingQuantity().subtract(tradeQty));

        if (buy.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
            buy.setStatus("FILLED");
        } else {
            buy.setStatus("PARTIAL");
        }

        if (sell.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
            sell.setStatus("FILLED");
        } else {
            sell.setStatus("PARTIAL");
        }

        // 🔥 NEXT: wallet updates (we'll add below)
    }

    public OrderBook getOrderBook(String symbol) {
        return orderBooks.computeIfAbsent(symbol, s -> new OrderBook());
    }
}