package com.crypto.exchange.repository;

import com.crypto.exchange.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByBuyerIdOrSellerIdOrderByIdDesc(Long buyerId, Long sellerId);

    List<Trade> findBySymbolOrderByIdDesc(String symbol);
}