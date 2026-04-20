package com.airawat.shoppingapp.service;
import com.airawat.shoppingapp.dto.NotificationRequestDTO;
import com.airawat.shoppingapp.dto.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {
    NotificationResponseDTO createNotification(NotificationRequestDTO requestDto);
    List<NotificationResponseDTO> getAllNotifications();
    NotificationResponseDTO getNotificationById(Long id);
    List<NotificationResponseDTO> getNotificationsByUserId(Long userId);
}