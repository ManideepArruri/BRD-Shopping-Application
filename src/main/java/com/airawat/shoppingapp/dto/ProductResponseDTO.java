package com.airawat.shoppingapp.dto;

import java.math.BigDecimal;

public class ProductResponseDTO {

    private Long productId;
    private String productName;
    private String description;
    private String category;
    private BigDecimal price;
    private String sku;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(Long productId, String productName, String description, String category, BigDecimal price, String sku) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.sku = sku;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}