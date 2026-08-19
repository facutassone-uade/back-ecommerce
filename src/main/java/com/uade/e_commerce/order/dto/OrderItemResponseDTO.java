package com.uade.e_commerce.order.dto;

import com.uade.e_commerce.product.dto.ProductResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {

    private Long id;

    private ProductResponseDTO product;

    private Integer quantity;
}
