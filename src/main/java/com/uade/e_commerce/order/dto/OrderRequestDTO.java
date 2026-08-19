package com.uade.e_commerce.order.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    private Long customerId;

    private LocalDate date;

    private Double total;

    private Boolean paid;

    private String paymentMethod;
}
