package com.uade.e_commerce.cart.dto;

import com.uade.e_commerce.product.dto.ProductSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {

    private Long id;

    private ProductSummaryDTO product;

    private Integer quantity;
}
