package com.uade.e_commerce.auth.dto;

import com.uade.e_commerce.customer.dto.CustomerResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;

    private CustomerResponseDTO customer;
}
