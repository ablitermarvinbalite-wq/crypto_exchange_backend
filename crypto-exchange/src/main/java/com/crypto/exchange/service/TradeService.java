package com.crypto.exchange.service;

import com.crypto.exchange.entity.Order;
import com.crypto.exchange.entity.Wallet;
import com.crypto.exchange.entity.Trade;
import com.crypto.exchange.repository.TradeRepository;
import com.crypto.exchange.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final WalletRepository walletRepository;
    private final TradeRepository tradeRepository;

    @Transactional
    public void executeTrade(Order buy, Order sell, BigDecimal qty, BigDecimal price) {

        BigDecimal total = price.multiply(qty);

        // 1. SAVE TRADE
        Trade trade = new Trade();
        trade.setBuyOrderId(buy.getId());
        trade.setSellOrderId(sell.getId());

        trade.setBuyerId(buy.getUserId());
        trade.setSellerId(sell.getUserId());

        trade.setSymbol(buy.getSymbol());

        trade.setPrice(price);
        trade.setQuantity(qty);
        trade.setTotal(total);

        tradeRepository.save(trade);

        // 2. UPDATE BUYER
        Wallet buyerUSDT = walletRepository
                .findByUserIdAndAsset(buy.getUserId(), "USDT")
                .orElseThrow();

        Wallet buyerBTC = walletRepository
                .findByUserIdAndAsset(buy.getUserId(), "BTC")
                .orElseThrow();

        buyerUSDT.setLockedBalance(
                buyerUSDT.getLockedBalance().subtract(total)
        );

        buyerBTC.setBalance(
                buyerBTC.getBalance().add(qty)
        );

        // 3. UPDATE SELLER
        Wallet sellerBTC = walletRepository
                .findByUserIdAndAsset(sell.getUserId(), "BTC")
                .orElseThrow();

        Wallet sellerUSDT = walletRepository
                .findByUserIdAndAsset(sell.getUserId(), "USDT")
                .orElseThrow();

        sellerBTC.setLockedBalance(
                sellerBTC.getLockedBalance().subtract(qty)
        );

        sellerUSDT.setBalance(
                sellerUSDT.getBalance().add(total)
        );

        walletRepository.save(buyerUSDT);
        walletRepository.save(buyerBTC);
        walletRepository.save(sellerBTC);
        walletRepository.save(sellerUSDT);
    }

    @Deprecated
    private void updateWallet(Long userId, String asset, BigDecimal amount, boolean add) {

        Wallet wallet = walletRepository
                .findByUserIdAndAsset(userId, asset)
                .orElseThrow();

        if (add) {
            wallet.setBalance(wallet.getBalance().add(amount));
        } else {
            wallet.setLockedBalance(wallet.getLockedBalance().subtract(amount));
        }

        walletRepository.save(wallet);
    }
}
