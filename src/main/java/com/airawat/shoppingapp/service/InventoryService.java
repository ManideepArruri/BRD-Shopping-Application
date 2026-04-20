package com.airawat.shoppingapp.service;

import com.airawat.shoppingapp.dto.InventoryRequestDTO;
import com.airawat.shoppingapp.dto.InventoryResponseDTO;

import java.util.List;

public interface InventoryService {
    InventoryResponseDTO createInventory(InventoryRequestDTO requestDto);
    List<InventoryResponseDTO> getAllInventory();
    InventoryResponseDTO getInventoryByProductId(Long productId);
    InventoryResponseDTO updateInventory(Long productId, InventoryRequestDTO requestDto);
    List<InventoryResponseDTO> getLowStockItems();
}