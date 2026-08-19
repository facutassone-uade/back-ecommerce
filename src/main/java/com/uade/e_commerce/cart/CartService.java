package com.uade.e_commerce.cart;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.customer.dto.AddressDTO;
import com.uade.e_commerce.cart.dto.CartRequestDTO;
import com.uade.e_commerce.cart.dto.CartResponseDTO;
import com.uade.e_commerce.customer.dto.CustomerSummaryDTO;
import com.uade.e_commerce.customer.Address;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.CustomerRepository;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;

    public CartService(CartRepository cartRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
    }

    public void delete(Long id) {
        cartRepository.deleteById(id);
    }

    public List<CartResponseDTO> list() {
        return cartRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CartResponseDTO findResponseById(Long id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            return null;
        }
        return toResponseDTO(cart);
    }

    public CartResponseDTO save(CartRequestDTO cartRequestDTO) {
        Customer customer = customerRepository.findById(cartRequestDTO.getCustomerId()).orElse(null);
        if (customer == null) {
            return null;
        }

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setDate(cartRequestDTO.getDate());
        cart.setQuantity(cartRequestDTO.getQuantity());
        cart.setDeliveryDate(cartRequestDTO.getDeliveryDate());

        Cart saved = cartRepository.save(cart);
        return toResponseDTO(saved);
    }

    public CartResponseDTO update(Long id, CartRequestDTO cartRequestDTO) {
        Cart existing = cartRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        Customer customer = customerRepository.findById(cartRequestDTO.getCustomerId()).orElse(null);
        if (customer == null) {
            return null;
        }

        existing.setCustomer(customer);
        existing.setDate(cartRequestDTO.getDate());
        existing.setQuantity(cartRequestDTO.getQuantity());
        existing.setDeliveryDate(cartRequestDTO.getDeliveryDate());

        Cart saved = cartRepository.save(existing);
        return toResponseDTO(saved);
    }

    private CartResponseDTO toResponseDTO(Cart cart) {
        CartResponseDTO responseDTO = new CartResponseDTO();
        responseDTO.setId(cart.getId());
        responseDTO.setCustomer(toCustomerSummaryDTO(cart.getCustomer()));
        responseDTO.setDate(cart.getDate());
        responseDTO.setQuantity(cart.getQuantity());
        responseDTO.setDeliveryDate(cart.getDeliveryDate());
        return responseDTO;
    }

    private CustomerSummaryDTO toCustomerSummaryDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerSummaryDTO summaryDTO = new CustomerSummaryDTO();
        summaryDTO.setId(customer.getId());
        summaryDTO.setName(customer.getName());
        summaryDTO.setLastName(customer.getLastName());
        summaryDTO.setPhone(customer.getPhone());
        summaryDTO.setAddress(toAddressDTO(customer.getAddress()));
        return summaryDTO;
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
}
