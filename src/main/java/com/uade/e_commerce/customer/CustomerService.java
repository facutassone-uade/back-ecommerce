package com.uade.e_commerce.customer;

import com.uade.e_commerce.auth.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uade.e_commerce.common.BusinessValidationException;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.common.ResponseDtoMapper;
import com.uade.e_commerce.customer.dto.CustomerRequestDTO;
import com.uade.e_commerce.customer.dto.CustomerResponseDTO;
import com.uade.e_commerce.auth.UserRepository;
import com.uade.e_commerce.order.OrderRepository;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ResponseDtoMapper responseDtoMapper;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository,
            OrderRepository orderRepository, ResponseDtoMapper responseDtoMapper) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.responseDtoMapper = responseDtoMapper;
    }

    public CustomerResponseDTO findResponseById(Long id) {
        Customer customer = findCustomerForAccess(id);
        return responseDtoMapper.toCustomerResponseDTO(customer);
    }

    public CustomerResponseDTO update(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer existing = findCustomerForAccess(id);
        Customer updated = responseDtoMapper.customerRequestDTOToEntity(existing, customerRequestDTO);
        return responseDtoMapper.toCustomerResponseDTO(customerRepository.save(updated));
    }

    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        if (!orderRepository.findByCustomerId(id).isEmpty()) {
            throw new BusinessValidationException("Cannot delete customer: orders are associated with this customer");
        }

        User user = customer.getUser();

        customerRepository.delete(customer);
        userRepository.delete(user);
    }

    private Customer findCustomerForAccess(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        if (isAdmin()) {
            return customer;
        }

        Long customerOwnerUserId = customer.getUser() != null ? customer.getUser().getId() : null;
        Long currentUserId = getCurrentUser().getId();
        if (customerOwnerUserId == null || !customerOwnerUserId.equals(currentUserId)) {
            throw new AccessDeniedException("You cannot access this customer");
        }

        return customer;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("Authenticated user not available");
        }
        return user;
    }

    private boolean isAdmin() {
        return getCurrentUser().getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
