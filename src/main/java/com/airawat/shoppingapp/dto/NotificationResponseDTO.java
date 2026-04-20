package com.airawat.shoppingapp.dto;


import java.time.LocalDateTime;

public class NotificationResponseDTO {

    private Long notificationId;
    private String notificationType;
    private String recipientReference;
    private String message;
    private String status;
    private Long userId;
    private LocalDateTime createdAt;

    public NotificationResponseDTO() {
    }

    public NotificationResponseDTO(Long notificationId, String notificationType, String recipientReference,
                                   String message, String status, Long userId, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.notificationType = notificationType;
        this.recipientReference = recipientReference;
        this.message = message;
        this.status = status;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getRecipientReference() {
        return recipientReference;
    }

    public void setRecipientReference(String recipientReference) {
        this.recipientReference = recipientReference;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}