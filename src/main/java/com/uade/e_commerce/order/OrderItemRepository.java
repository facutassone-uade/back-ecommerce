package com.uade.e_commerce.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.order.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
