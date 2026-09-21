package com.uade.e_commerce.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String message;
    private Long userId;
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}

