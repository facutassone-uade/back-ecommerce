package com.uade.e_commerce.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;

    private CustomerSummaryDTO customer;

    private LocalDate date;

    private Double total;

    private Boolean paid;

    private String paymentMethod;

    private List<OrderItemResponseDTO> items;
}
