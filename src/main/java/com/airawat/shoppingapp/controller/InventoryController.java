package com.airawat.shoppingapp.controller;

import com.airawat.shoppingapp.dto.InventoryRequestDto;
import com.airawat.shoppingapp.dto.InventoryResponseDto;
import com.airawat.shoppingapp.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponseDto> createInventory(@Valid @RequestBody InventoryRequestDto requestDto) {
        return new ResponseEntity<>(inventoryService.createInventory(requestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDto>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> updateInventory(@PathVariable Long productId,
                                                                @Valid @RequestBody InventoryRequestDto requestDto) {
        return ResponseEntity.ok(inventoryService.updateInventory(productId, requestDto));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponseDto>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }
}