package com.airawat.shoppingapp.repository;

import com.airawat.shoppingapp.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct_ProductId(Long productId);
    List<Inventory> findByAvailableQuantityLessThanEqual(Integer reorderLevel);
}