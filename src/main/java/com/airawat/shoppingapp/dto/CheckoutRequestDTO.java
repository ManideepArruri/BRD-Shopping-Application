package com.airawat.shoppingapp.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
 
import java.util.List;
 
public class CheckoutRequestDTO {
 
    @NotNull(message = "User id is required")
    private Long userId;
 
    @Valid
    @NotEmpty(message = "At least one item is required")
    private List<CheckoutItemRequestDTO> items;
 
    public CheckoutRequestDTO() {
    }
 
    public Long getUserId() {
        return userId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
    }
 
    public List<CheckoutItemRequestDTO> getItems() {
        return items;
    }
 
    public void setItems(List<CheckoutItemRequestDTO> items) {
        this.items = items;
    }
}