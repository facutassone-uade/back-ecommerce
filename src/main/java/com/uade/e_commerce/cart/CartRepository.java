package com.uade.e_commerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.cart.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
