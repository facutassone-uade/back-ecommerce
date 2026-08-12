package com.uade.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
