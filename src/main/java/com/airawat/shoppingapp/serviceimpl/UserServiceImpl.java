package com.airawat.shoppingapp.serviceimpl;

import com.airawat.shoppingapp.dto.UserRequestDTO;
import com.airawat.shoppingapp.dto.UserResponseDTO;
import com.airawat.shoppingapp.model.User;
import com.airawat.shoppingapp.exception.BadRequestException;
import com.airawat.shoppingapp.exception.ResourceNotFoundException;
import com.airawat.shoppingapp.repository.UserRepository;
import com.airawat.shoppingapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO requestDto) {
        logger.info("Creating user with email: {}", requestDto.getEmail());
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            logger.warn("Duplicate email detected: {}", requestDto.getEmail());
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setPhone(requestDto.getPhone());
        user.setRole(requestDto.getRole());

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with id: {}", savedUser.getUserId());
        return mapToResponse(savedUser);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Fetching all users");
        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        logger.info("Found {} users", users.size());
        return users;
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        return mapToResponse(user);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDto) {
        logger.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found for update with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setPhone(requestDto.getPhone());
        user.setRole(requestDto.getRole());

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with id: {}", updatedUser.getUserId());
        return mapToResponse(updatedUser);
    }

    private UserResponseDTO mapToResponse(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}