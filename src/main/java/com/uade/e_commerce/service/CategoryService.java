package com.uade.e_commerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.CategoryRequestDTO;
import com.uade.e_commerce.dto.CategoryResponseDTO;
import com.uade.e_commerce.model.Category;
import com.uade.e_commerce.repository.CategoryRepository;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<CategoryResponseDTO> list() {
        return categoryRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CategoryResponseDTO findResponseById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return null;
        }
        return toResponseDTO(category);
    }

    public CategoryResponseDTO save(CategoryRequestDTO categoryRequestDTO) {
        Category category = new Category();
        category.setName(categoryRequestDTO.getName());
        Category saved = categoryRepository.save(category);
        return toResponseDTO(saved);
    }

    public CategoryResponseDTO update(Long id, CategoryRequestDTO categoryRequestDTO) {
        Category existing = categoryRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setName(categoryRequestDTO.getName());
        Category saved = categoryRepository.save(existing);
        return toResponseDTO(saved);
    }

    private CategoryResponseDTO toResponseDTO(Category category) {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(category.getId());
        responseDTO.setName(category.getName());
        return responseDTO;
    }
}
