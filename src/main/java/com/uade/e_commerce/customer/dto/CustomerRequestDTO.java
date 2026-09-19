package com.uade.e_commerce.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {

    @NotNull(message = "National ID is required")
    private Long nationalId;


    @NotNull(message = "Phone is required")
    private Long phone;

    @Valid
    private AddressDTO address;
}
