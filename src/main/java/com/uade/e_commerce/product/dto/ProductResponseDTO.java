package com.uade.e_commerce.product.dto;

import java.util.List;

import com.uade.e_commerce.category.dto.CategoryResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private List<CategoryResponseDTO> categories;
}
