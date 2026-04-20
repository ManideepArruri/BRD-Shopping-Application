package com.airawat.shoppingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.airawat.shoppingapp.dto.*;
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
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductRequestDTO createValidProductRequest() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setProductName("iPhone 15");
        dto.setDescription("Latest Apple smartphone");
        dto.setCategory("Electronics");
        dto.setPrice(new BigDecimal("999.99"));
        dto.setSku("APPLE-IP15-001");
        return dto;
    }

    @Test
    @Order(1)
    void testCreateProduct_Success() throws Exception {
        ProductRequestDTO request = createValidProductRequest();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").exists())
                .andExpect(jsonPath("$.productName").value("iPhone 15"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.sku").value("APPLE-IP15-001"));
    }

    @Test
    @Order(2)
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(3)
    void testGetProductById_Success() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("iPhone 15"));
    }

    @Test
    @Order(4)
    void testGetProductById_NotFound() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    @Test
    @Order(5)
    void testUpdateProduct_Success() throws Exception {
        ProductRequestDTO request = createValidProductRequest();
        request.setProductName("iPhone 15 Pro");
        request.setPrice(new BigDecimal("1199.99"));

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.price").value(1199.99));
    }

    @Test
    @Order(6)
    void testCreateProduct_DuplicateSku() throws Exception {
        ProductRequestDTO request = createValidProductRequest();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("SKU already exists"));
    }

    @Test
    @Order(7)
    void testCreateProduct_ValidationError_BlankName() throws Exception {
        ProductRequestDTO request = createValidProductRequest();
        request.setProductName("");
        request.setSku("UNIQUE-SKU-001");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @Order(8)
    void testCreateProduct_ValidationError_NegativePrice() throws Exception {
        ProductRequestDTO request = createValidProductRequest();
        request.setPrice(new BigDecimal("-5.00"));
        request.setSku("UNIQUE-SKU-002");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void testDeleteProduct() throws Exception {
        // Create a product to delete
        ProductRequestDTO request = createValidProductRequest();
        request.setSku("DELETE-ME-001");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Delete product with id 2 (the second created product)
        mockMvc.perform(delete("/api/products/2"))
                .andExpect(status().isOk());

        // Verify it's gone
        mockMvc.perform(get("/api/products/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    void testDeleteProduct_NotFound() throws Exception {
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
