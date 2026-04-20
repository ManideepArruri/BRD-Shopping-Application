package com.airawat.shoppingapp.service;

import com.airawat.shoppingapp.dto.InventoryRequestDto;
import com.airawat.shoppingapp.dto.InventoryResponseDto;

import java.util.List;

public interface InventoryService {
    InventoryResponseDto createInventory(InventoryRequestDto requestDto);
    List<InventoryResponseDto> getAllInventory();
    InventoryResponseDto getInventoryByProductId(Long productId);
    InventoryResponseDto updateInventory(Long productId, InventoryRequestDto requestDto);
    List<InventoryResponseDto> getLowStockItems();
}