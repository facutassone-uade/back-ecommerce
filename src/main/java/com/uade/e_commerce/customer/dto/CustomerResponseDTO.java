package com.uade.e_commerce.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {

    private Long id;

    private String name;

    private String lastName;

    private Long nationalId;

    private String email;

    private Long phone;

    private AddressDTO address;

    private String username;
}
