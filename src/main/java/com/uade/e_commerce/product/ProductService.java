package com.uade.e_commerce.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.common.ResponseDtoMapper;
import com.uade.e_commerce.product.dto.ProductRequestDTO;
import com.uade.e_commerce.product.dto.ProductResponseDTO;
import com.uade.e_commerce.category.Category;
import com.uade.e_commerce.category.CategoryRepository;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ResponseDtoMapper dtoMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            ResponseDtoMapper dtoMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.dtoMapper = dtoMapper;
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productRepository.delete(product);
    }

    public List<ProductResponseDTO> list() {
        return productRepository.findAllByOrderByNameAsc().stream()
                .map(dtoMapper::toProductResponseDTO)
                .toList();
    }

    public ProductResponseDTO findResponseById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return dtoMapper.toProductResponseDTO(product);
    }

    public ProductResponseDTO save(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());

        Product saved = productRepository.save(product);
        return dtoMapper.toProductResponseDTO(saved);
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());

        Product saved = productRepository.save(product);
        return dtoMapper.toProductResponseDTO(saved);
    }

    public ProductResponseDTO addCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        if (product.getCategories() == null) {
            product.setCategories(new ArrayList<>());
        }
        if (!product.getCategories().contains(category)) {
            product.getCategories().add(category);
        }
        Product saved = productRepository.save(product);
        return dtoMapper.toProductResponseDTO(saved);
    }

    public ProductResponseDTO removeCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        if (product.getCategories() == null || product.getCategories().isEmpty()) {
            throw new ResourceNotFoundException("Product with id " + productId + " has no associated categories");
        }
        boolean removed = product.getCategories().removeIf(category -> category.getId().equals(categoryId));
        if (!removed) {
            throw new ResourceNotFoundException(
                    "Product with id " + productId + " does not have category with id " + categoryId + " associated");
        }
        Product saved = productRepository.save(product);
        return dtoMapper.toProductResponseDTO(saved);
    }
}
