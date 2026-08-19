package com.uade.e_commerce.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {

    private Long customerId;

    private LocalDate date;

    private Number quantity;

    private LocalDate deliveryDate;
}
