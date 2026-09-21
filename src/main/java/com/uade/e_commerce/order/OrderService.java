package com.uade.e_commerce.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uade.e_commerce.auth.User;
import com.uade.e_commerce.common.BusinessValidationException;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.common.ResponseDtoMapper;
import com.uade.e_commerce.order.dto.OrderItemRequestDTO;
import com.uade.e_commerce.order.dto.OrderResponseDTO;
import com.uade.e_commerce.order.dto.OrderRequestDTO;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.product.Product;
import com.uade.e_commerce.customer.CustomerRepository;
import com.uade.e_commerce.product.ProductRepository;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ResponseDtoMapper dtoMapper;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
            ProductRepository productRepository, OrderItemRepository orderItemRepository,
            ResponseDtoMapper dtoMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.dtoMapper = dtoMapper;
    }

    public void delete(Long id) {
        Order order = findOrderForAccess(id);
        orderRepository.delete(order);
    }

    public List<OrderResponseDTO> list() {
        if (isAdmin()) {
            return orderRepository.findAll().stream()
                    .map(dtoMapper::toOrderResponseDTO)
                    .toList();
        }

        Customer customer = getCurrentCustomer();
        return orderRepository.findByCustomerId(customer.getId()).stream()
                .map(dtoMapper::toOrderResponseDTO)
                .toList();
    }

    public List<OrderResponseDTO> findOrdersByUserId(Long userId) {
        if (!isAdmin() && !getCurrentUser().getId().equals(userId)) {
            throw new AccessDeniedException("You cannot access another user's orders");
        }

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id " + userId));

        return orderRepository.findByCustomerId(customer.getId()).stream()
                .map(dtoMapper::toOrderResponseDTO)
                .toList();
    }

    public OrderResponseDTO findResponseById(Long id) {
        Order order = findOrderForAccess(id);
        return dtoMapper.toOrderResponseDTO(order);
    }

    public OrderResponseDTO save(OrderRequestDTO orderRequestDTO) {
        Long customerId = orderRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new BusinessValidationException("Cannot create order: customerId is required");
        }

        if (!isAdmin()) {
            Long currentCustomerId = getCurrentCustomer().getId();
            if (!currentCustomerId.equals(customerId)) {
                throw new AccessDeniedException("You cannot create an order for another user");
            }
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        Order order = new Order();
        order.setCustomer(customer);
        order.setDate(orderRequestDTO.getDate());
        order.setTotal(orderRequestDTO.getTotal());
        order.setPaid(orderRequestDTO.getPaid());
        order.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        Order saved = orderRepository.save(order);
        return dtoMapper.toOrderResponseDTO(saved);
    }

    public OrderResponseDTO update(Long id, OrderRequestDTO orderRequestDTO) {
        Order existing = findOrderForAccess(id);
        Long customerId = orderRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new BusinessValidationException("Cannot update order: customerId is required");
        }

        if (!isAdmin()) {
            Long currentCustomerId = getCurrentCustomer().getId();
            if (!currentCustomerId.equals(customerId)) {
                throw new AccessDeniedException("You cannot update another user's order");
            }
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        existing.setCustomer(customer);
        existing.setDate(orderRequestDTO.getDate());
        existing.setTotal(orderRequestDTO.getTotal());
        existing.setPaid(orderRequestDTO.getPaid());
        existing.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        Order saved = orderRepository.save(existing);
        return dtoMapper.toOrderResponseDTO(saved);
    }

    public OrderResponseDTO addItem(Long orderId, OrderItemRequestDTO orderItemRequestDTO) {
        Order order = findOrderForAccess(orderId);
        Long productId = orderItemRequestDTO.getProductId();
        if (productId == null) {
            throw new BusinessValidationException("Cannot add item: productId is required");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(orderItemRequestDTO.getQuantity());
        orderItemRepository.saveAndFlush(item);

        Order refreshed = orderRepository.findById(orderId).orElse(order);
        return dtoMapper.toOrderResponseDTO(refreshed);
    }

    public OrderResponseDTO removeItem(Long orderId, Long itemId) {
        Order order = findOrderForAccess(orderId);
        if (!orderItemRepository.existsByIdAndOrderId(itemId, orderId)) {
            throw new ResourceNotFoundException("Order item", itemId);
        }
        orderItemRepository.deleteById(itemId);
        orderItemRepository.flush();
        Order refreshed = orderRepository.findById(orderId).orElse(order);
        return dtoMapper.toOrderResponseDTO(refreshed);
    }

    private Order findOrderForAccess(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (isAdmin()) {
            return order;
        }

        Long orderOwnerUserId = order.getCustomer() != null && order.getCustomer().getUser() != null
                ? order.getCustomer().getUser().getId()
                : null;
        Long currentUserId = getCurrentUser().getId();
        if (orderOwnerUserId == null || !orderOwnerUserId.equals(currentUserId)) {
            throw new AccessDeniedException("You cannot access this order");
        }

        return order;
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

    private Customer getCurrentCustomer() {
        User currentUser = getCurrentUser();
        return customerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id " + currentUser.getId()));
    }
}
