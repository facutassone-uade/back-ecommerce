package com.uade.e_commerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.AddressDTO;
import com.uade.e_commerce.dto.CustomerSummaryDTO;
import com.uade.e_commerce.dto.OrderItemRequestDTO;
import com.uade.e_commerce.dto.OrderItemResponseDTO;
import com.uade.e_commerce.dto.OrderRequestDTO;
import com.uade.e_commerce.dto.OrderResponseDTO;
import com.uade.e_commerce.dto.ProductResponseDTO;
import com.uade.e_commerce.model.Address;
import com.uade.e_commerce.model.Customer;
import com.uade.e_commerce.model.Order;
import com.uade.e_commerce.model.OrderItem;
import com.uade.e_commerce.model.Product;
import com.uade.e_commerce.repository.CustomerRepository;
import com.uade.e_commerce.repository.OrderItemRepository;
import com.uade.e_commerce.repository.OrderRepository;
import com.uade.e_commerce.repository.ProductRepository;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository,
            ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    public List<OrderResponseDTO> list() {
        return orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public OrderResponseDTO findResponseById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        return toResponseDTO(order);
    }

    public OrderResponseDTO save(OrderRequestDTO orderRequestDTO) {
        Customer customer = customerRepository.findById(orderRequestDTO.getCustomerId()).orElse(null);
        if (customer == null) {
            return null;
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setDate(orderRequestDTO.getDate());
        order.setTotal(orderRequestDTO.getTotal());
        order.setPaid(orderRequestDTO.getPaid());
        order.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        Order saved = orderRepository.save(order);
        return toResponseDTO(saved);
    }

    public OrderResponseDTO update(Long id, OrderRequestDTO orderRequestDTO) {
        Order existing = orderRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        Customer customer = customerRepository.findById(orderRequestDTO.getCustomerId()).orElse(null);
        if (customer == null) {
            return null;
        }

        existing.setCustomer(customer);
        existing.setDate(orderRequestDTO.getDate());
        existing.setTotal(orderRequestDTO.getTotal());
        existing.setPaid(orderRequestDTO.getPaid());
        existing.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        Order saved = orderRepository.save(existing);
        return toResponseDTO(saved);
    }

    public OrderResponseDTO addItem(Long orderId, OrderItemRequestDTO orderItemRequestDTO) {
        Order order = orderRepository.findById(orderId).orElse(null);
        Product product = productRepository.findById(orderItemRequestDTO.getProductId()).orElse(null);
        if (order == null || product == null) {
            return null;
        }

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(orderItemRequestDTO.getQuantity());
        orderItemRepository.save(item);

        Order refreshed = orderRepository.findById(orderId).orElse(order);
        return toResponseDTO(refreshed);
    }

    public OrderResponseDTO removeItem(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        orderItemRepository.deleteById(itemId);
        Order refreshed = orderRepository.findById(orderId).orElse(order);
        return toResponseDTO(refreshed);
    }

    private OrderResponseDTO toResponseDTO(Order order) {
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

    private OrderItemResponseDTO toOrderItemResponseDTO(OrderItem item) {
        OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
        itemDTO.setId(item.getId());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setProduct(toProductResponseDTO(item.getProduct()));
        return itemDTO;
    }

    private ProductResponseDTO toProductResponseDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponseDTO productDTO = new ProductResponseDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        return productDTO;
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
