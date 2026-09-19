package com.uade.e_commerce.customer;

import com.uade.e_commerce.auth.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.common.ResponseDtoMapper;
import com.uade.e_commerce.customer.dto.CustomerRequestDTO;
import com.uade.e_commerce.customer.dto.CustomerResponseDTO;
import com.uade.e_commerce.auth.UserRepository;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ResponseDtoMapper responseDtoMapper;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository,
            ResponseDtoMapper responseDtoMapper) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.responseDtoMapper = responseDtoMapper;
    }

    public CustomerResponseDTO findResponseById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        return responseDtoMapper.toCustomerResponseDTO(customer);
    }

    public CustomerResponseDTO update(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        Customer updated = responseDtoMapper.customerRequestDTOToEntity(existing, customerRequestDTO);
        return responseDtoMapper.toCustomerResponseDTO(customerRepository.save(updated));
    }

    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        User user = customer.getUser();

        customerRepository.delete(customer);
        userRepository.delete(user);
    }
}



