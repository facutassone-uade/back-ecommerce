package com.uade.e_commerce.cart.dto;

import java.time.LocalDate;

import com.uade.e_commerce.customer.dto.CustomerSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {

    private Long id;

    private CustomerSummaryDTO customer;

    private LocalDate date;

    private Number quantity;

    private LocalDate deliveryDate;
}
