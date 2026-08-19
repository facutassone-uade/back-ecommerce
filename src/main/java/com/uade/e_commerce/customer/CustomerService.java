package com.uade.e_commerce.customer;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.customer.dto.AddressDTO;
import com.uade.e_commerce.customer.dto.CustomerRequestDTO;
import com.uade.e_commerce.customer.dto.CustomerResponseDTO;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    public List<CustomerResponseDTO> list() {
        return customerRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CustomerResponseDTO findResponseById(Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return null;
        }
        return toResponseDTO(customer);
    }

    public CustomerResponseDTO save(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        applyRequestDTO(customer, customerRequestDTO);
        Customer saved = customerRepository.save(customer);
        return toResponseDTO(saved);
    }

    public CustomerResponseDTO update(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer existing = customerRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        applyRequestDTO(existing, customerRequestDTO);
        Customer saved = customerRepository.save(existing);
        return toResponseDTO(saved);
    }

    private void applyRequestDTO(Customer customer, CustomerRequestDTO customerRequestDTO) {
        customer.setName(customerRequestDTO.getName());
        customer.setLastName(customerRequestDTO.getLastName());
        customer.setNationalId(customerRequestDTO.getNationalId());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setPhone(customerRequestDTO.getPhone());
        customer.setAddress(toAddress(customerRequestDTO.getAddress()));
        customer.setUsername(customerRequestDTO.getUsername());
        customer.setPassword(customerRequestDTO.getPassword());
    }

    private Address toAddress(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setZipCode(addressDTO.getZipCode());
        address.setCountry(addressDTO.getCountry());
        return address;
    }

    private AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet(address.getStreet());
        addressDTO.setCity(address.getCity());
        addressDTO.setZipCode(address.getZipCode());
        addressDTO.setCountry(address.getCountry());
        return addressDTO;
    }

    private CustomerResponseDTO toResponseDTO(Customer customer) {
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setId(customer.getId());
        responseDTO.setName(customer.getName());
        responseDTO.setLastName(customer.getLastName());
        responseDTO.setNationalId(customer.getNationalId());
        responseDTO.setEmail(customer.getEmail());
        responseDTO.setPhone(customer.getPhone());
        responseDTO.setAddress(toAddressDTO(customer.getAddress()));
        responseDTO.setUsername(customer.getUsername());
        return responseDTO;
    }
}
