package com.airawat.shoppingapp.serviceimpl;

import com.airawat.shoppingapp.dto.NotificationRequestDTO;
import com.airawat.shoppingapp.dto.NotificationResponseDTO;
import com.airawat.shoppingapp.model.Notification;
import com.airawat.shoppingapp.model.User;
import com.airawat.shoppingapp.exception.ResourceNotFoundException;
import com.airawat.shoppingapp.repository.NotificationRepository;
import com.airawat.shoppingapp.repository.UserRepository;
import com.airawat.shoppingapp.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDto) {
        Notification notification = new Notification();
        notification.setNotificationType(requestDto.getNotificationType());
        notification.setRecipientReference(requestDto.getRecipientReference());
        notification.setMessage(requestDto.getMessage());
        notification.setStatus(requestDto.getStatus());

        if (requestDto.getUserId() != null) {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));
            notification.setUser(user);
        }

        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationResponseDTO> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponseDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return mapToResponse(notification);
    }

    @Override
    public List<NotificationResponseDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUser_UserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
