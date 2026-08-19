package com.uade.e_commerce.cart;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.cart.dto.CartItemRequestDTO;
import com.uade.e_commerce.cart.dto.CartItemResponseDTO;
import com.uade.e_commerce.cart.dto.CartRequestDTO;
import com.uade.e_commerce.cart.dto.CartResponseDTO;
import com.uade.e_commerce.customer.Address;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.CustomerRepository;
import com.uade.e_commerce.customer.dto.AddressDTO;
import com.uade.e_commerce.customer.dto.CustomerSummaryDTO;
import com.uade.e_commerce.product.Product;
import com.uade.e_commerce.product.ProductRepository;
import com.uade.e_commerce.product.dto.ProductResponseDTO;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, CustomerRepository customerRepository,
            ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
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
        existing.setDeliveryDate(cartRequestDTO.getDeliveryDate());

        Cart saved = cartRepository.save(existing);
        return toResponseDTO(saved);
    }

    public CartResponseDTO addItem(Long cartId, CartItemRequestDTO cartItemRequestDTO) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        Product product = productRepository.findById(cartItemRequestDTO.getProductId()).orElse(null);
        if (cart == null || product == null) {
            return null;
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(cartItemRequestDTO.getQuantity());
        cartItemRepository.saveAndFlush(item);

        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return toResponseDTO(refreshed);
    }

    public CartResponseDTO removeItem(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            return null;
        }
        cartItemRepository.deleteById(itemId);
        cartItemRepository.flush();
        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return toResponseDTO(refreshed);
    }

    public CartResponseDTO clearItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            return null;
        }
        cartItemRepository.deleteAll(cartItemRepository.findByCartId(cartId));
        cartItemRepository.flush();
        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return toResponseDTO(refreshed);
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
