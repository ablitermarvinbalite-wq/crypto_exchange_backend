CREATE INDEX idx_symbol ON trades(symbol);
CREATE INDEX idx_buyer ON trades(buyer_id);
CREATE INDEX idx_seller ON trades(seller_id);