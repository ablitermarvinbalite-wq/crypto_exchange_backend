package com.crypto.exchange.repository;

import com.crypto.exchange.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByIdDesc(Long userId);

}
