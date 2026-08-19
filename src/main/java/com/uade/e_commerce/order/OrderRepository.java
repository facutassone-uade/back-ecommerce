package com.uade.e_commerce.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
