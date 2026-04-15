package com.airawat.shoppingapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    @UpdateTimestamp
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    public Inventory() {}

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

	public Inventory(Product product, Integer availableQuantity, Integer reorderLevel, LocalDateTime lastUpdatedAt) {
		this.product = product;
		this.availableQuantity = availableQuantity;
		this.reorderLevel = reorderLevel;
		this.lastUpdatedAt = lastUpdatedAt;
	}
    
}