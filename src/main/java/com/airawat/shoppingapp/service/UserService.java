package com.airawat.shoppingapp.service;

import com.airawat.shoppingapp.dto.UserRequestDTO;
import com.airawat.shoppingapp.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO requestDto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, UserRequestDTO requestDto);
}