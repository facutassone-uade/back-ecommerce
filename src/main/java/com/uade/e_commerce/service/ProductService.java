package com.uade.e_commerce.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.CategoryResponseDTO;
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

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<ProductResponseDTO> list() {
        return productRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProductResponseDTO findResponseById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return null;
        }
        return toResponseDTO(product);
    }

    public ProductResponseDTO save(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());

        Product saved = productRepository.save(product);
        return toResponseDTO(saved);
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
        return toResponseDTO(saved);
    }

    public ProductResponseDTO addCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId).orElse(null);
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (product == null || category == null) {
            return null;
        }
        if (product.getCategories() == null) {
            product.setCategories(new ArrayList<>());
        }
        if (!product.getCategories().contains(category)) {
            product.getCategories().add(category);
        }
        Product saved = productRepository.save(product);
        return toResponseDTO(saved);
    }

    public ProductResponseDTO removeCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getCategories() == null) {
            return null;
        }
        product.getCategories().removeIf(category -> category.getId().equals(categoryId));
        Product saved = productRepository.save(product);
        return toResponseDTO(saved);
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(product.getId());
        responseDTO.setName(product.getName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setStock(product.getStock());
        if (product.getCategories() != null) {
            responseDTO.setCategories(product.getCategories().stream()
                    .map(this::toCategoryResponseDTO)
                    .toList());
        }
        return responseDTO;
    }

    private CategoryResponseDTO toCategoryResponseDTO(Category category) {
        CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }
}
