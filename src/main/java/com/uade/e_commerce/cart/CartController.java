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

import com.uade.e_commerce.cart.dto.CartItemRequestDTO;
import com.uade.e_commerce.cart.dto.CartRequestDTO;
import com.uade.e_commerce.cart.dto.CartResponseDTO;

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
        CartResponseDTO cart = cartService.findResponseById(id);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> create(@RequestBody CartRequestDTO cartRequestDTO) {
        CartResponseDTO created = cartService.save(cartRequestDTO);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDTO> update(@PathVariable Long id, @RequestBody CartRequestDTO cartRequestDTO) {
        CartResponseDTO updated = cartService.update(id, cartRequestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cartService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CartResponseDTO> addItem(@PathVariable Long id, @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        CartResponseDTO updated = cartService.addItem(id, cartItemRequestDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        CartResponseDTO updated = cartService.removeItem(id, itemId);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/items")
    public ResponseEntity<CartResponseDTO> clearItems(@PathVariable Long id) {
        CartResponseDTO updated = cartService.clearItems(id);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}
