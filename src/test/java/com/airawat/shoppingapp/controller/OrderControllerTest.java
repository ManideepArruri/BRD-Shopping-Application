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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private void setupTestData() throws Exception {
        // Create a user
        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setEmail("alice@example.com");
        user.setPhone("9876543210");
        user.setRole("CUSTOMER");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        // Create a product
        ProductRequestDTO product = new ProductRequestDTO();
        product.setProductName("Wireless Mouse");
        product.setDescription("Ergonomic wireless mouse");
        product.setCategory("Accessories");
        product.setPrice(new BigDecimal("29.99"));
        product.setSku("ACC-MOUSE-001");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());

        // Create a second product
        ProductRequestDTO product2 = new ProductRequestDTO();
        product2.setProductName("Keyboard");
        product2.setDescription("Mechanical keyboard");
        product2.setCategory("Accessories");
        product2.setPrice(new BigDecimal("79.99"));
        product2.setSku("ACC-KB-001");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product2)))
                .andExpect(status().isCreated());

        // Add inventory for product 1 (quantity=50)
        InventoryRequestDTO inv1 = new InventoryRequestDTO();
        inv1.setProductId(1L);
        inv1.setAvailableQuantity(50);
        inv1.setReorderLevel(5);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv1)))
                .andExpect(status().isCreated());

        // Add inventory for product 2 (quantity=3, low stock scenario)
        InventoryRequestDTO inv2 = new InventoryRequestDTO();
        inv2.setProductId(2L);
        inv2.setAvailableQuantity(3);
        inv2.setReorderLevel(5);

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv2)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(1)
    void testCheckout_Success() throws Exception {
        setupTestData();

        CheckoutItemRequestDTO item = new CheckoutItemRequestDTO();
        item.setProductId(1L);
        item.setQuantity(2);

        CheckoutRequestDTO checkout = new CheckoutRequestDTO();
        checkout.setUserId(1L);
        checkout.setItems(List.of(item));

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.customerName").value("Alice Smith"))
                .andExpect(jsonPath("$.orderStatus").value("PLACED"))
                .andExpect(jsonPath("$.totalAmount").value(59.98))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    @Order(2)
    void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(3)
    void testGetOrderById_Success() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.orderStatus").value("PLACED"));
    }

    @Test
    @Order(4)
    void testGetOrderById_NotFound() throws Exception {
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: 999"));
    }

    @Test
    @Order(5)
    void testGetOrdersByUserId() throws Exception {
        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @Order(6)
    void testCheckout_InsufficientStock() throws Exception {
        CheckoutItemRequestDTO item = new CheckoutItemRequestDTO();
        item.setProductId(1L);
        item.setQuantity(9999);

        CheckoutRequestDTO checkout = new CheckoutRequestDTO();
        checkout.setUserId(1L);
        checkout.setItems(List.of(item));

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Insufficient stock")));
    }

    @Test
    @Order(7)
    void testCheckout_UserNotFound() throws Exception {
        CheckoutItemRequestDTO item = new CheckoutItemRequestDTO();
        item.setProductId(1L);
        item.setQuantity(1);

        CheckoutRequestDTO checkout = new CheckoutRequestDTO();
        checkout.setUserId(999L);
        checkout.setItems(List.of(item));

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }

    @Test
    @Order(8)
    void testCheckout_ProductNotFound() throws Exception {
        CheckoutItemRequestDTO item = new CheckoutItemRequestDTO();
        item.setProductId(999L);
        item.setQuantity(1);

        CheckoutRequestDTO checkout = new CheckoutRequestDTO();
        checkout.setUserId(1L);
        checkout.setItems(List.of(item));

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found")));
    }

    @Test
    @Order(9)
    void testCheckout_ValidationError_EmptyItems() throws Exception {
        CheckoutRequestDTO checkout = new CheckoutRequestDTO();
        checkout.setUserId(1L);
        checkout.setItems(List.of());

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    void testCheckout_LowStockTriggersNotification() throws Exception {
        // Buy from product 2 which has low stock (qty=3, reorderLevel=5)
        CheckoutItemRequestDTO item = new CheckoutItemRequestDTO();
        item.setProductId(2L);
        item.setQuantity(1);

        CheckoutRequestDTO checkout = new CheckoutRequestDTO();
        checkout.setUserId(1L);
        checkout.setItems(List.of(item));

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isCreated());

        // Verify notifications were created (checkout + low stock)
        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @Order(11)
    void testCheckout_MultipleItems() throws Exception {
        // Reset inventory for product 1
        InventoryRequestDTO inv = new InventoryRequestDTO();
        inv.setProductId(1L);
        inv.setAvailableQuantity(100);
        inv.setReorderLevel(5);

        mockMvc.perform(put("/api/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv)))
                .andExpect(status().isOk());

        // Reset inventory for product 2
        InventoryRequestDTO inv2 = new InventoryRequestDTO();
        inv2.setProductId(2L);
        inv2.setAvailableQuantity(50);
        inv2.setReorderLevel(5);

        mockMvc.perform(put("/api/inventory/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv2)))
                .andExpect(status().isOk());

        // Checkout with multiple items
        CheckoutItemRequestDTO item1 = new CheckoutItemRequestDTO();
        item1.setProductId(1L);
        item1.setQuantity(3);

        CheckoutItemRequestDTO item2 = new CheckoutItemRequestDTO();
        item2.setProductId(2L);
        item2.setQuantity(2);

        CheckoutRequestDTO checkout = new CheckoutRequestDTO();
        checkout.setUserId(1L);
        checkout.setItems(List.of(item1, item2));

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkout)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.totalAmount").isNumber());
    }
}