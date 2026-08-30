package com.uade.e_commerce.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.category.dto.CategoryResponseDTO;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.product.dto.ProductRequestDTO;
import com.uade.e_commerce.product.dto.ProductResponseDTO;
import com.uade.e_commerce.category.Category;
import com.uade.e_commerce.category.CategoryRepository;

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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());

        Product saved = productRepository.save(product);
        return toResponseDTO(saved);
    }

    public ProductResponseDTO addCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", categoryId));
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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
        if (product.getCategories() == null) {
            throw new ResourceNotFoundException("El producto con id " + productId + " no tiene categorías asociadas");
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
