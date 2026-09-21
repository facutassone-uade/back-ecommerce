package com.uade.e_commerce.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.auth.dto.AuthResponseDTO;
import com.uade.e_commerce.auth.dto.LoginRequestDTO;
import com.uade.e_commerce.auth.dto.RegisterRequestDTO;
import com.uade.e_commerce.common.DuplicateResourceException;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.CustomerRepository;
import com.uade.e_commerce.common.ResponseDtoMapper;

@Service
@Transactional
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResponseDtoMapper responseDtoMapper;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository,
            CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
            ResponseDtoMapper responseDtoMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.responseDtoMapper = responseDtoMapper;
    }

    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }

        User user = new User();
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(savedUser);
        Customer savedCustomer = customerRepository.save(customer);

        return responseDtoMapper.toAuthResponseDTO("User registered successfully", savedUser, savedCustomer.getId());
    }

    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        String email = requestDTO.getEmail();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, requestDTO.getPassword()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " does not exist"));

        Long customerId = customerRepository.findByUserId(user.getId())
                .map(Customer::getId)
                .orElse(null);
        return responseDtoMapper.toAuthResponseDTO("Authentication successful", user, customerId);
    }
}



