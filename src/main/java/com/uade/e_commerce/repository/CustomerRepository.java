package com.uade.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
