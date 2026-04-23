package com.crypto.exchange.service;

import com.crypto.exchange.dto.WalletRequest;
import com.crypto.exchange.dto.WalletResponse;
import com.crypto.exchange.entity.Wallet;
import com.crypto.exchange.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public WalletResponse deposit(WalletRequest request) {

        validateAmount(request.getAmount());

        Wallet wallet = walletRepository
                .findByUserIdAndAsset(request.getUserId(), request.getAsset())
                .orElseGet(() -> createWallet(request));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        return map(walletRepository.save(wallet));
    }

    @Transactional
    public WalletResponse withdraw(WalletRequest request) {

        validateAmount(request.getAmount());

        Wallet wallet = walletRepository
                .findByUserIdAndAsset(request.getUserId(), request.getAsset())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));

        return map(walletRepository.save(wallet));
    }

    public WalletResponse getBalance(Long userId, String asset) {

        Wallet wallet = walletRepository
                .findByUserIdAndAsset(userId, asset)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return map(wallet);
    }

    // 🧠 Helpers

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }

    private Wallet createWallet(WalletRequest request) {
        Wallet wallet = new Wallet();
        wallet.setUserId(request.getUserId());
        wallet.setAsset(request.getAsset());
        wallet.setBalance(BigDecimal.ZERO);
        return wallet;
    }

    private WalletResponse map(Wallet wallet) {
        return WalletResponse.builder()
                .userId(wallet.getUserId())
                .asset(wallet.getAsset())
                .balance(wallet.getBalance())
                .build();
    }

    @Transactional
    public void lockBalance(Long userId, String asset, BigDecimal amount) {

        Wallet wallet = walletRepository
                .findByUserIdAndAsset(userId, asset)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setLockedBalance(wallet.getLockedBalance().add(amount));

        walletRepository.save(wallet);
    }

    @Transactional
    public void unlockBalance(Long userId, String asset, BigDecimal amount) {

        Wallet wallet = walletRepository
                .findByUserIdAndAsset(userId, asset)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getLockedBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Invalid unlock amount");
        }

        wallet.setLockedBalance(wallet.getLockedBalance().subtract(amount));
        wallet.setBalance(wallet.getBalance().add(amount));

        walletRepository.save(wallet);
    }

}
