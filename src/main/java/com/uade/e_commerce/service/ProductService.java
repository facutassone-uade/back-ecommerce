package com.uade.e_commerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.ProductRequestDTO;
import com.uade.e_commerce.dto.ProductResponseDTO;
import com.uade.e_commerce.model.Category;
import com.uade.e_commerce.model.Product;
import com.uade.e_commerce.repository.CategoryRepository;
import com.uade.e_commerce.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> list() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponseDTO save(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());

        Product saved = productRepository.save(product);

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(saved.getId());
        responseDTO.setName(saved.getName());
        responseDTO.setDescription(saved.getDescription());
        responseDTO.setPrice(saved.getPrice());
        responseDTO.setStock(saved.getStock());

        return responseDTO;
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return null;
        }
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());

        Product saved = productRepository.save(product);

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(saved.getId());
        responseDTO.setName(saved.getName());
        responseDTO.setDescription(saved.getDescription());
        responseDTO.setPrice(saved.getPrice());
        responseDTO.setStock(saved.getStock());

        return responseDTO;
    }

    public Product addCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId).orElse(null);
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (product == null || category == null) {
            return null;
        }
        if (product.getCategories() == null) {
            product.setCategories(new java.util.ArrayList<>());
        }
        if (!product.getCategories().contains(category)) {
            product.getCategories().add(category);
        }
        return productRepository.save(product);
    }

    public Product removeCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getCategories() == null) {
            return null;
        }
        product.getCategories().removeIf(category -> category.getId().equals(categoryId));
        return productRepository.save(product);
    }
}
