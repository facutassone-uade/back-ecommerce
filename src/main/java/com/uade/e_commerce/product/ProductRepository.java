package com.uade.e_commerce.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository for the Product entity.
 * Provides methods to perform CRUD operations against the database.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds products with a price lower than the given value.
     *
     * @param price the maximum price to search for.
     * @return a list of products with a price lower than the given value.
     */
    List<Product> findByPriceLessThan(Double price);
}
