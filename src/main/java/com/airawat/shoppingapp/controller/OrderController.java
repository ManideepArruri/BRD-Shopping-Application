package com.airawat.shoppingapp.controller;


import com.airawat.shoppingapp.dto.CheckoutRequestDTO;
import com.airawat.shoppingapp.dto.OrderResponseDTO;
import com.airawat.shoppingapp.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(@Valid @RequestBody CheckoutRequestDTO requestDto) {
        return new ResponseEntity<>(orderService.checkout(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/api/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/api/orders/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/api/orders/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
}