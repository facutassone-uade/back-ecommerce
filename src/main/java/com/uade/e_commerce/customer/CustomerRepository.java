package com.uade.e_commerce.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.customer.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
