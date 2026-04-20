package com.airawat.shoppingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.airawat.shoppingapp.dto.InventoryRequestDTO;
import com.airawat.shoppingapp.dto.ProductRequestDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private void createProduct(String name, String sku) throws Exception {
        ProductRequestDTO product = new ProductRequestDTO();
        product.setProductName(name);
        product.setDescription("Test product");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("100.00"));
        product.setSku(sku);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(1)
    void testCreateInventory_Success() throws Exception {
        // First create a product
        createProduct("Test Laptop", "INV-TEST-001");

        // Then add inventory for it
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(1L);
        request.setAvailableQuantity(100);
        request.setReorderLevel(10);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inventoryId").exists())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("Test Laptop"))
                .andExpect(jsonPath("$.availableQuantity").value(100))
                .andExpect(jsonPath("$.reorderLevel").value(10));
    }

    @Test
    @Order(2)
    void testGetAllInventory() throws Exception {
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(3)
    void testGetInventoryByProductId_Success() throws Exception {
        mockMvc.perform(get("/api/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.availableQuantity").value(100));
    }

    @Test
    @Order(4)
    void testGetInventoryByProductId_NotFound() throws Exception {
        mockMvc.perform(get("/api/inventory/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Inventory not found for product id: 999"));
    }

    @Test
    @Order(5)
    void testUpdateInventory_Success() throws Exception {
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(1L);
        request.setAvailableQuantity(200);
        request.setReorderLevel(20);

        mockMvc.perform(put("/api/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQuantity").value(200))
                .andExpect(jsonPath("$.reorderLevel").value(20));
    }

    @Test
    @Order(6)
    void testGetLowStockItems_NoLowStock() throws Exception {
        mockMvc.perform(get("/api/inventory/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(7)
    void testGetLowStockItems_WithLowStock() throws Exception {
        // Update inventory to have low stock (quantity <= reorderLevel)
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(1L);
        request.setAvailableQuantity(5);
        request.setReorderLevel(10);

        mockMvc.perform(put("/api/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/inventory/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].availableQuantity").value(5));
    }

    @Test
    @Order(8)
    void testCreateInventory_DuplicateProduct() throws Exception {
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(1L);
        request.setAvailableQuantity(50);
        request.setReorderLevel(5);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Inventory already exists")));
    }

    @Test
    @Order(9)
    void testCreateInventory_ValidationError_NegativeQuantity() throws Exception {
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(1L);
        request.setAvailableQuantity(-5);
        request.setReorderLevel(10);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    void testCreateInventory_ProductNotFound() throws Exception {
        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(999L);
        request.setAvailableQuantity(50);
        request.setReorderLevel(5);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}