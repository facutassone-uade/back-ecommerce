package com.uade.e_commerce.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.common.ResponseDtoMapper;
import com.uade.e_commerce.category.dto.CategoryRequestDTO;
import com.uade.e_commerce.category.dto.CategoryResponseDTO;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ResponseDtoMapper dtoMapper;

    public CategoryService(CategoryRepository categoryRepository, ResponseDtoMapper dtoMapper) {
        this.categoryRepository = categoryRepository;
        this.dtoMapper = dtoMapper;
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        categoryRepository.delete(category);
    }

    public List<CategoryResponseDTO> list() {
        return categoryRepository.findAll().stream()
                .map(dtoMapper::toCategoryResponseDTO)
                .toList();
    }

    public CategoryResponseDTO findResponseById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return dtoMapper.toCategoryResponseDTO(category);
    }

    public CategoryResponseDTO save(CategoryRequestDTO categoryRequestDTO) {
        Category category = new Category();
        category.setName(categoryRequestDTO.getName());
        Category saved = categoryRepository.save(category);
        return dtoMapper.toCategoryResponseDTO(saved);
    }

    public CategoryResponseDTO update(Long id, CategoryRequestDTO categoryRequestDTO) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        existing.setName(categoryRequestDTO.getName());
        Category saved = categoryRepository.save(existing);
        return dtoMapper.toCategoryResponseDTO(saved);
    }

}
