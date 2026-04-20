package com.airawat.shoppingapp.serviceimpl;

import com.airawat.shoppingapp.dto.NotificationRequestDTO;
import com.airawat.shoppingapp.dto.NotificationResponseDTO;
import com.airawat.shoppingapp.model.Notification;
import com.airawat.shoppingapp.model.User;
import com.airawat.shoppingapp.exception.ResourceNotFoundException;
import com.airawat.shoppingapp.repository.NotificationRepository;
import com.airawat.shoppingapp.repository.UserRepository;
import com.airawat.shoppingapp.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
public class NotificationServiceImpl implements NotificationService {
 
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
 
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
 
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }
 
    @Override
    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDto) {
        logger.info("Creating notification of type: {} for user id: {}", requestDto.getNotificationType(), requestDto.getUserId());
        Notification notification = new Notification();
        notification.setNotificationType(requestDto.getNotificationType());
        notification.setRecipientReference(requestDto.getRecipientReference());
        notification.setMessage(requestDto.getMessage());
        notification.setStatus(requestDto.getStatus());
 
        if (requestDto.getUserId() != null) {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> {
                        logger.error("User not found with id: {}", requestDto.getUserId());
                        return new ResourceNotFoundException("User not found with id: " + requestDto.getUserId());
                    });
            notification.setUser(user);
        }
 
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification created with id: {}, type: {}", savedNotification.getNotificationId(), savedNotification.getNotificationType());
        return mapToResponse(savedNotification);
    }
 
    @Override
    public List<NotificationResponseDTO> getAllNotifications() {
        logger.info("Fetching all notifications");
        List<NotificationResponseDTO> notifications = notificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        logger.info("Found {} notifications", notifications.size());
        return notifications;
    }
 
    @Override
    public NotificationResponseDTO getNotificationById(Long id) {
        logger.info("Fetching notification with id: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Notification not found with id: {}", id);
                    return new ResourceNotFoundException("Notification not found with id: " + id);
                });
        return mapToResponse(notification);
    }
 
    @Override
    public List<NotificationResponseDTO> getNotificationsByUserId(Long userId) {
        logger.info("Fetching notifications for user id: {}", userId);
        List<NotificationResponseDTO> notifications = notificationRepository.findByUser_UserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        logger.info("Found {} notifications for user id: {}", notifications.size(), userId);
        return notifications;
    }
 
    private NotificationResponseDTO mapToResponse(Notification notification) {
        return new NotificationResponseDTO(
                notification.getNotificationId(),
                notification.getNotificationType(),
                notification.getRecipientReference(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getUser() != null ? notification.getUser().getUserId() : null,
                notification.getCreatedAt()
        );
    }
}