package com.uade.e_commerce.category;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
