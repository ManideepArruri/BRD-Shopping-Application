package com.airawat.shoppingapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.airawat.shoppingapp.dto.NotificationRequestDTO;
import com.airawat.shoppingapp.dto.UserRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private void createTestUser() throws Exception {
        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName("Bob");
        user.setLastName("Jones");
        user.setEmail("bob.jones@example.com");
        user.setPhone("5551234567");
        user.setRole("CUSTOMER");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(1)
    void testCreateNotification_Success() throws Exception {
        createTestUser();

        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setNotificationType("CHECKOUT_CONFIRMATION");
        request.setRecipientReference("bob.jones@example.com");
        request.setMessage("Your order has been placed successfully");
        request.setStatus("SENT");
        request.setUserId(1L);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").exists())
                .andExpect(jsonPath("$.notificationType").value("CHECKOUT_CONFIRMATION"))
                .andExpect(jsonPath("$.recipientReference").value("bob.jones@example.com"))
                .andExpect(jsonPath("$.message").value("Your order has been placed successfully"))
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @Order(2)
    void testCreateNotification_LowStock() throws Exception {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setNotificationType("LOW_STOCK");
        request.setRecipientReference("ADMIN");
        request.setMessage("Low stock alert for product: Laptop");
        request.setStatus("SENT");
        request.setUserId(1L);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationType").value("LOW_STOCK"));
    }

    @Test
    @Order(3)
    void testGetAllNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @Order(4)
    void testGetNotificationById_Success() throws Exception {
        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1))
                .andExpect(jsonPath("$.notificationType").value("CHECKOUT_CONFIRMATION"));
    }

    @Test
    @Order(5)
    void testGetNotificationById_NotFound() throws Exception {
        mockMvc.perform(get("/api/notifications/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Notification not found with id: 999"));
    }

    @Test
    @Order(6)
    void testGetNotificationsByUserId() throws Exception {
        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @Order(7)
    void testGetNotificationsByUserId_Empty() throws Exception {
        mockMvc.perform(get("/api/notifications/user/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(8)
    void testCreateNotification_ValidationError_BlankType() throws Exception {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setNotificationType("");
        request.setRecipientReference("test@test.com");
        request.setMessage("Test message");
        request.setStatus("SENT");
        request.setUserId(1L);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void testCreateNotification_UserNotFound() throws Exception {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setNotificationType("CHECKOUT_CONFIRMATION");
        request.setRecipientReference("unknown@test.com");
        request.setMessage("Test");
        request.setStatus("SENT");
        request.setUserId(999L);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }
}