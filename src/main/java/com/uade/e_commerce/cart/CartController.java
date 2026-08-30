package com.uade.e_commerce.cart;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.cart.dto.CartCheckoutRequestDTO;
import com.uade.e_commerce.cart.dto.CartItemRequestDTO;
import com.uade.e_commerce.cart.dto.CartRequestDTO;
import com.uade.e_commerce.cart.dto.CartResponseDTO;
import com.uade.e_commerce.order.dto.OrderResponseDTO;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartResponseDTO> list() {
        return cartService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.findResponseById(id));
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> create(@RequestBody CartRequestDTO cartRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.save(cartRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDTO> update(@PathVariable Long id, @RequestBody CartRequestDTO cartRequestDTO) {
        return ResponseEntity.ok(cartService.update(id, cartRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cartService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CartResponseDTO> addItem(@PathVariable Long id, @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        return ResponseEntity.ok(cartService.addItem(id, cartItemRequestDTO));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(id, itemId));
    }

    @DeleteMapping("/{id}/items")
    public ResponseEntity<CartResponseDTO> clearItems(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.clearItems(id));
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(@PathVariable Long id, @RequestBody CartCheckoutRequestDTO checkoutRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.checkout(id, checkoutRequestDTO));
    }
}
