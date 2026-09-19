package com.uade.e_commerce.cart;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.cart.dto.CartCheckoutRequestDTO;
import com.uade.e_commerce.cart.dto.CartItemRequestDTO;
import com.uade.e_commerce.cart.dto.CartRequestDTO;
import com.uade.e_commerce.cart.dto.CartResponseDTO;
import com.uade.e_commerce.common.BusinessValidationException;
import com.uade.e_commerce.common.ResourceNotFoundException;
import com.uade.e_commerce.common.ResponseDtoMapper;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.CustomerRepository;
import com.uade.e_commerce.order.Order;
import com.uade.e_commerce.order.OrderItem;
import com.uade.e_commerce.order.OrderItemRepository;
import com.uade.e_commerce.order.OrderRepository;
import com.uade.e_commerce.order.dto.OrderResponseDTO;
import com.uade.e_commerce.product.Product;
import com.uade.e_commerce.product.ProductRepository;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ResponseDtoMapper dtoMapper;

    public CartService(CartRepository cartRepository, CustomerRepository customerRepository,
            ProductRepository productRepository, CartItemRepository cartItemRepository,
            OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            ResponseDtoMapper dtoMapper) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.dtoMapper = dtoMapper;
    }

    public void delete(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", id));
        cartRepository.delete(cart);
    }

    public List<CartResponseDTO> list() {
        return cartRepository.findAll().stream()
                .map(dtoMapper::toCartResponseDTO)
                .toList();
    }

    public CartResponseDTO findResponseById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", id));
        return dtoMapper.toCartResponseDTO(cart);
    }

    public CartResponseDTO save(CartRequestDTO cartRequestDTO) {
        Long customerId = cartRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new BusinessValidationException("Cannot create cart: customerId is required");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setDate(cartRequestDTO.getDate());
        cart.setDeliveryDate(cartRequestDTO.getDeliveryDate());

        Cart saved = cartRepository.save(cart);
        return dtoMapper.toCartResponseDTO(saved);
    }

    public CartResponseDTO update(Long id, CartRequestDTO cartRequestDTO) {
        Cart existing = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", id));
        Long customerId = cartRequestDTO.getCustomerId();
        if (customerId == null) {
            throw new BusinessValidationException("Cannot update cart: customerId is required");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        existing.setCustomer(customer);
        existing.setDate(cartRequestDTO.getDate());
        existing.setDeliveryDate(cartRequestDTO.getDeliveryDate());

        Cart saved = cartRepository.save(existing);
        return dtoMapper.toCartResponseDTO(saved);
    }

    public CartResponseDTO addItem(Long cartId, CartItemRequestDTO cartItemRequestDTO) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
        Long productId = cartItemRequestDTO.getProductId();
        if (productId == null) {
            throw new BusinessValidationException("Cannot add item: productId is required");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        Integer quantity = cartItemRequestDTO.getQuantity();

        if (quantity == null || quantity <= 0) {
            throw new BusinessValidationException("Cannot add item: quantity must be greater than 0");
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        int alreadyInCart = existingItem != null ? existingItem.getQuantity() : 0;
        if (product.getStock() < alreadyInCart + quantity) {
            throw new BusinessValidationException(
                    "Cannot add item: insufficient stock for product with id " + productId);
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.saveAndFlush(existingItem);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartItemRepository.saveAndFlush(item);
        }

        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return dtoMapper.toCartResponseDTO(refreshed);
    }

    public CartResponseDTO removeItem(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
        if (!cartItemRepository.existsByIdAndCartId(itemId, cartId)) {
            throw new ResourceNotFoundException("Cart item", itemId);
        }
        cartItemRepository.deleteById(itemId);
        cartItemRepository.flush();
        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return dtoMapper.toCartResponseDTO(refreshed);
    }

    public CartResponseDTO clearItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));
        cartItemRepository.deleteAll(cartItemRepository.findByCartId(cartId));
        cartItemRepository.flush();
        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return dtoMapper.toCartResponseDTO(refreshed);
    }

    public OrderResponseDTO checkout(Long cartId, CartCheckoutRequestDTO checkoutRequestDTO) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", cartId));

        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        if (items.isEmpty()) {
            throw new BusinessValidationException("Cannot checkout: cart is empty");
        }

        for (CartItem item : items) {
            if (item.getProduct().getStock() < item.getQuantity()) {
                throw new BusinessValidationException(
                        "Cannot checkout: insufficient stock for product with id "
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
        return dtoMapper.toOrderResponseDTO(refreshedOrder);
    }
}
