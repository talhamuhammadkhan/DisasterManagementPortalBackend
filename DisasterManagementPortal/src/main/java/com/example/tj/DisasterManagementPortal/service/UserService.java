package com.example.tj.DisasterManagementPortal.service;

import com.example.tj.DisasterManagementPortal.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(Long id);

    User updateUser(User user);

    void deleteUser(Long id);

    List<User> getAllUsers();

    User getUserByEmail(String email);

    boolean isValidPassword(String password);

    boolean isEmailTaken(String email);
}
