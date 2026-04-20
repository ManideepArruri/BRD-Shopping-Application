package com.airawat.shoppingapp.service;

import com.airawat.shoppingapp.dto.CheckoutRequestDTO;
import com.airawat.shoppingapp.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO checkout(CheckoutRequestDTO requestDto);
    List<OrderResponseDTO> getAllOrders();
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByUserId(Long userId);
}