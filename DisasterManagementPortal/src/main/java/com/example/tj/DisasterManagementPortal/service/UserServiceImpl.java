package com.example.tj.DisasterManagementPortal.service;

import com.example.tj.DisasterManagementPortal.model.User;
import com.example.tj.DisasterManagementPortal.repository.ApplicationRepository;
import com.example.tj.DisasterManagementPortal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        if (userRepository.existsById(user.getId())) {
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        applicationRepository.deleteByVolunteerId(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // Password validation method
    public boolean isValidPassword(String password) {
        // Password should be at least 8 characters, have at least 1 number, and 1 special character
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        return password.matches(passwordPattern);
    }

    // Check if the email is already in use
    public boolean isEmailTaken(String email) {
        return getUserByEmail(email) != null;
    }

}
