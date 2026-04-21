CREATE TABLE wallets (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         asset VARCHAR(20) NOT NULL,   -- BTC, USDT, ETH
                         balance DECIMAL(18,8) NOT NULL DEFAULT 0,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                         UNIQUE KEY uk_user_asset (user_id, asset)
);