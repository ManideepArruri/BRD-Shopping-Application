package com.airawat.shoppingapp.dto;

public class InventoryResponseDto {

    private Long inventoryId;
    private Long productId;
    private String productName;
    private Integer availableQuantity;
    private Integer reorderLevel;

    public InventoryResponseDto() {
    }

    public InventoryResponseDto(Long inventoryId, Long productId, String productName, Integer availableQuantity, Integer reorderLevel) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
        this.reorderLevel = reorderLevel;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}