CREATE TABLE trades (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,

                        buy_order_id BIGINT NOT NULL,
                        sell_order_id BIGINT NOT NULL,

                        buyer_id BIGINT NOT NULL,
                        seller_id BIGINT NOT NULL,

                        symbol VARCHAR(20) NOT NULL,        -- BTCUSDT

                        price DECIMAL(19,8) NOT NULL,
                        quantity DECIMAL(19,8) NOT NULL,

                        total DECIMAL(19,8) NOT NULL,       -- price * quantity

                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);