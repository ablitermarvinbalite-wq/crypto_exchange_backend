package com.crypto.exchange.controller;

import com.crypto.exchange.dto.TradeResponse;
import com.crypto.exchange.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    // 👤 USER HISTORY
    @GetMapping("/user/{userId}")
    public List<TradeResponse> getUserTrades(@PathVariable Long userId) {
        return tradeService.getUserTrades(userId);
    }

    // 📊 MARKET HISTORY
    @GetMapping("/market/{symbol}")
    public List<TradeResponse> getMarketTrades(@PathVariable String symbol) {
        return tradeService.getMarketTrades(symbol);
    }
}
