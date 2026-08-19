package com.uade.e_commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryDTO {

    private Long id;

    private String name;

    private String lastName;

    private Long phone;

    private AddressDTO address;
}
