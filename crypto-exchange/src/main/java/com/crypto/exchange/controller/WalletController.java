package com.crypto.exchange.controller;

import com.crypto.exchange.dto.WalletRequest;
import com.crypto.exchange.dto.WalletResponse;
import com.crypto.exchange.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/deposit")
    public WalletResponse deposit(@RequestBody WalletRequest request) {
        return walletService.deposit(request);
    }

    @PostMapping("/withdraw")
    public WalletResponse withdraw(@RequestBody WalletRequest request) {
        return walletService.withdraw(request);
    }

    @GetMapping
    public WalletResponse getBalance(
            @RequestParam Long userId,
            @RequestParam String asset
    ) {
        return walletService.getBalance(userId, asset);
    }
}