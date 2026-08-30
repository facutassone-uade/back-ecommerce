package com.uade.e_commerce.order;

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

import com.uade.e_commerce.order.dto.OrderItemRequestDTO;
import com.uade.e_commerce.order.dto.OrderRequestDTO;
import com.uade.e_commerce.order.dto.OrderResponseDTO;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponseDTO> list() {
        return orderService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findResponseById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.save(orderRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> update(@PathVariable Long id, @RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.ok(orderService.update(id, orderRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponseDTO> addItem(@PathVariable Long id, @RequestBody OrderItemRequestDTO orderItemRequestDTO) {
        return ResponseEntity.ok(orderService.addItem(id, orderItemRequestDTO));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<OrderResponseDTO> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        return ResponseEntity.ok(orderService.removeItem(id, itemId));
    }
}
