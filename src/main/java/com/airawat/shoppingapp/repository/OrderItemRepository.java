package com.airawat.shoppingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airawat.shoppingapp.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
