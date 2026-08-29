package com.uade.e_commerce.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.common.BusinessValidationException;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.customer.dto.AddressDTO;
import com.uade.e_commerce.customer.dto.CustomerSummaryDTO;
import com.uade.e_commerce.order.dto.OrderItemRequestDTO;
import com.uade.e_commerce.order.dto.OrderItemResponseDTO;
import com.uade.e_commerce.order.dto.OrderRequestDTO;
import com.uade.e_commerce.order.dto.OrderResponseDTO;
import com.uade.e_commerce.product.dto.ProductSummaryDTO;
import com.uade.e_commerce.customer.Address;
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
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", id));
        return toResponseDTO(order);
    }

    public OrderResponseDTO save(OrderRequestDTO orderRequestDTO) {
        Long customerId = orderRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new BusinessValidationException("No se puede crear la orden: customerId es obligatorio");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessValidationException(
                        "No se puede crear la orden: el cliente con id " + customerId + " no existe"));

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
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", id));
        Long customerId = orderRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new BusinessValidationException("No se puede actualizar la orden: customerId es obligatorio");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", customerId));

        existing.setCustomer(customer);
        existing.setDate(orderRequestDTO.getDate());
        existing.setTotal(orderRequestDTO.getTotal());
        existing.setPaid(orderRequestDTO.getPaid());
        existing.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        Order saved = orderRepository.save(existing);
        return toResponseDTO(saved);
    }

    public OrderResponseDTO addItem(Long orderId, OrderItemRequestDTO orderItemRequestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", orderId));
        Long productId = orderItemRequestDTO.getProductId();
        if (productId == null) {
            throw new BusinessValidationException("No se puede agregar el ítem: productId es obligatorio");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(orderItemRequestDTO.getQuantity());
        orderItemRepository.saveAndFlush(item);

        Order refreshed = orderRepository.findById(orderId).orElse(order);
        return toResponseDTO(refreshed);
    }

    public OrderResponseDTO removeItem(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", orderId));
        orderItemRepository.deleteById(itemId);
        orderItemRepository.flush();
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
        itemDTO.setProduct(toProductSummaryDTO(item.getProduct()));
        return itemDTO;
    }

    private ProductSummaryDTO toProductSummaryDTO(Product product) {
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
