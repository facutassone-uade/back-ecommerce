package com.uade.e_commerce.cart;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.cart.dto.CartCheckoutRequestDTO;
import com.uade.e_commerce.cart.dto.CartItemRequestDTO;
import com.uade.e_commerce.cart.dto.CartItemResponseDTO;
import com.uade.e_commerce.cart.dto.CartRequestDTO;
import com.uade.e_commerce.cart.dto.CartResponseDTO;
import com.uade.e_commerce.common.BusinessValidationException;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.customer.Address;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.CustomerRepository;
import com.uade.e_commerce.customer.dto.AddressDTO;
import com.uade.e_commerce.customer.dto.CustomerSummaryDTO;
import com.uade.e_commerce.order.Order;
import com.uade.e_commerce.order.OrderItem;
import com.uade.e_commerce.order.OrderItemRepository;
import com.uade.e_commerce.order.OrderRepository;
import com.uade.e_commerce.order.dto.OrderItemResponseDTO;
import com.uade.e_commerce.order.dto.OrderResponseDTO;
import com.uade.e_commerce.product.Product;
import com.uade.e_commerce.product.ProductRepository;
import com.uade.e_commerce.product.dto.ProductSummaryDTO;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CartService(CartRepository cartRepository, CustomerRepository customerRepository,
            ProductRepository productRepository, CartItemRepository cartItemRepository,
            OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
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
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));
        return toResponseDTO(cart);
    }

    public CartResponseDTO save(CartRequestDTO cartRequestDTO) {
        Long customerId = cartRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new BusinessValidationException("No se puede crear el carrito: customerId es obligatorio");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", customerId));

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setDate(cartRequestDTO.getDate());
        cart.setDeliveryDate(cartRequestDTO.getDeliveryDate());

        Cart saved = cartRepository.save(cart);
        return toResponseDTO(saved);
    }

    public CartResponseDTO update(Long id, CartRequestDTO cartRequestDTO) {
        Cart existing = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", id));
        Long customerId = cartRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new BusinessValidationException("No se puede actualizar el carrito: customerId es obligatorio");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", customerId));

        existing.setCustomer(customer);
        existing.setDate(cartRequestDTO.getDate());
        existing.setDeliveryDate(cartRequestDTO.getDeliveryDate());

        Cart saved = cartRepository.save(existing);
        return toResponseDTO(saved);
    }

    public CartResponseDTO addItem(Long cartId, CartItemRequestDTO cartItemRequestDTO) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", cartId));
        Long productId = cartItemRequestDTO.getProductId();
        if (productId == null) {
            throw new BusinessValidationException("No se puede agregar el ítem: productId es obligatorio");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        Integer quantity = cartItemRequestDTO.getQuantity();
        int alreadyInCart = cartItemRepository.findByCartId(cartId).stream()
                .filter(existing -> existing.getProduct().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();
        if (product.getStock() < alreadyInCart + quantity) {
            throw new BusinessValidationException(
                    "No se puede agregar el ítem: stock insuficiente para el producto con id " + productId);
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        cartItemRepository.saveAndFlush(item);

        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return toResponseDTO(refreshed);
    }

    public CartResponseDTO removeItem(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", cartId));
        cartItemRepository.deleteById(itemId);
        cartItemRepository.flush();
        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return toResponseDTO(refreshed);
    }

    public CartResponseDTO clearItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", cartId));
        cartItemRepository.deleteAll(cartItemRepository.findByCartId(cartId));
        cartItemRepository.flush();
        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return toResponseDTO(refreshed);
    }

    public OrderResponseDTO checkout(Long cartId, CartCheckoutRequestDTO checkoutRequestDTO) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", cartId));

        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        if (items.isEmpty()) {
            throw new BusinessValidationException("No se puede finalizar la compra: el carrito está vacío");
        }

        for (CartItem item : items) {
            if (item.getProduct().getStock() < item.getQuantity()) {
                throw new BusinessValidationException(
                        "No se puede finalizar la compra: stock insuficiente para el producto con id "
                                + item.getProduct().getId());
            }
        }

        double total = 0.0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setDate(LocalDate.now());
        order.setTotal(total);
        order.setPaid(false);
        order.setPaymentMethod(checkoutRequestDTO.getPaymentMethod());
        Order savedOrder = orderRepository.save(order);

        for (CartItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteAll(items);
        cartItemRepository.flush();

        Order refreshedOrder = orderRepository.findById(savedOrder.getId()).orElse(savedOrder);
        return toOrderResponseDTO(refreshedOrder);
    }

    private OrderResponseDTO toOrderResponseDTO(Order order) {
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

    private CartResponseDTO toResponseDTO(Cart cart) {
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

    private CartItemResponseDTO toCartItemResponseDTO(CartItem item) {
        CartItemResponseDTO itemDTO = new CartItemResponseDTO();
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
