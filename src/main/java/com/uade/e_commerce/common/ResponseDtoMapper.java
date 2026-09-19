package com.uade.e_commerce.common;

import org.springframework.stereotype.Component;

import com.uade.e_commerce.auth.User;
import com.uade.e_commerce.auth.dto.AuthResponseDTO;
import com.uade.e_commerce.cart.Cart;
import com.uade.e_commerce.cart.CartItem;
import com.uade.e_commerce.cart.dto.CartItemResponseDTO;
import com.uade.e_commerce.cart.dto.CartResponseDTO;
import com.uade.e_commerce.category.Category;
import com.uade.e_commerce.category.dto.CategoryResponseDTO;
import com.uade.e_commerce.customer.Address;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.dto.AddressDTO;
import com.uade.e_commerce.customer.dto.CustomerRequestDTO;
import com.uade.e_commerce.customer.dto.CustomerResponseDTO;
import com.uade.e_commerce.customer.dto.CustomerSummaryDTO;
import com.uade.e_commerce.order.Order;
import com.uade.e_commerce.order.OrderItem;
import com.uade.e_commerce.order.dto.OrderItemResponseDTO;
import com.uade.e_commerce.order.dto.OrderResponseDTO;
import com.uade.e_commerce.product.Product;
import com.uade.e_commerce.product.dto.ProductResponseDTO;
import com.uade.e_commerce.product.dto.ProductSummaryDTO;

@Component
public class ResponseDtoMapper {

    public AuthResponseDTO toAuthResponseDTO(String message, User user, Long customerId) {
        if (user == null) {
            return null;
        }
        return AuthResponseDTO.builder()
                .message(message)
                .userId(user.getId())
                .customerId(customerId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : "USER")
                .build();
    }

    public CustomerResponseDTO toCustomerResponseDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setId(customer.getId());
        responseDTO.setNationalId(customer.getNationalId());
        responseDTO.setPhone(customer.getPhone());
        responseDTO.setAddress(addressEntityToDTO(customer.getAddress()));
        responseDTO.setUserId(customer.getUser() != null ? customer.getUser().getId() : null);
        return responseDTO;
    }

    public Customer customerRequestDTOToEntity(Customer customer, CustomerRequestDTO requestDTO) {
        customer.setNationalId(requestDTO.getNationalId());
        customer.setPhone(requestDTO.getPhone());
        customer.setAddress(addressDTOToEntity(requestDTO.getAddress()));
        return customer;
    }

    public Address addressDTOToEntity(AddressDTO addressDTO) {
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

    public AddressDTO addressEntityToDTO(Address address) {
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

    public OrderResponseDTO toOrderResponseDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setCustomer(toCustomerSummaryDTO(order.getCustomer()));
        responseDTO.setDate(order.getDate());
        responseDTO.setTotal(order.getTotal());
        responseDTO.setPaid(order.getPaid());
        responseDTO.setPaymentMethod(order.getPaymentMethod());
        if (order.getItems() != null) {
            responseDTO.setItems(order.getItems().stream()
                    .map(this::toOrderItemResponseDTO)
                    .toList());
        }
        return responseDTO;
    }

    public OrderItemResponseDTO toOrderItemResponseDTO(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
        itemDTO.setId(item.getId());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setProduct(toProductSummaryDTO(item.getProduct()));
        return itemDTO;
    }

    public ProductSummaryDTO toProductSummaryDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductSummaryDTO productDTO = new ProductSummaryDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        return productDTO;
    }

    public CustomerSummaryDTO toCustomerSummaryDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
         CustomerSummaryDTO summaryDTO = new CustomerSummaryDTO();
         summaryDTO.setId(customer.getId());
         if (customer.getUser() != null) {
             summaryDTO.setName(customer.getUser().getFirstName());
             summaryDTO.setLastName(customer.getUser().getLastName());
         }
        summaryDTO.setPhone(customer.getPhone());
        summaryDTO.setAddress(addressEntityToDTO(customer.getAddress()));
        return summaryDTO;
    }

    public CartResponseDTO toCartResponseDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
        CartResponseDTO responseDTO = new CartResponseDTO();
        responseDTO.setId(cart.getId());
        responseDTO.setCustomer(toCustomerSummaryDTO(cart.getCustomer()));
        responseDTO.setDate(cart.getDate());
        responseDTO.setDeliveryDate(cart.getDeliveryDate());
        if (cart.getItems() != null) {
            responseDTO.setItems(cart.getItems().stream()
                    .map(this::toCartItemResponseDTO)
                    .toList());
        }
        return responseDTO;
    }

    public CartItemResponseDTO toCartItemResponseDTO(CartItem item) {
        if (item == null) {
            return null;
        }
        CartItemResponseDTO itemDTO = new CartItemResponseDTO();
        itemDTO.setId(item.getId());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setProduct(toProductSummaryDTO(item.getProduct()));
        return itemDTO;
    }

    public CategoryResponseDTO toCategoryResponseDTO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }

    public ProductResponseDTO toProductResponseDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(product.getId());
        responseDTO.setName(product.getName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setStock(product.getStock());
        if (product.getCategories() != null) {
            responseDTO.setCategories(product.getCategories().stream()
                    .map(this::toCategoryResponseDTO)
                    .toList());
        }
        return responseDTO;
    }
}
