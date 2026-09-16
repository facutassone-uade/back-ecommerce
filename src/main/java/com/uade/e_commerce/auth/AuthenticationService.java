package com.uade.e_commerce.auth;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.auth.dto.AuthResponseDTO;
import com.uade.e_commerce.auth.dto.LoginRequestDTO;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.CustomerRepository;
import com.uade.e_commerce.customer.CustomerService;
import com.uade.e_commerce.customer.dto.CustomerRequestDTO;
import com.uade.e_commerce.customer.dto.CustomerResponseDTO;

@Service
public class AuthenticationService {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(CustomerService customerService, CustomerRepository customerRepository,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO register(CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO created = customerService.save(customerRequestDTO);
        String token = jwtService.generateToken(created.getEmail(), Map.of("role", "CUSTOMER"));
        return new AuthResponseDTO(token, created);
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));

        Customer customer = customerRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado tras autenticarse"));

        String token = jwtService.generateToken(customer.getEmail(), Map.of("role", customer.getRole().name()));
        return new AuthResponseDTO(token, customerService.toResponseDTO(customer));
    }
}
