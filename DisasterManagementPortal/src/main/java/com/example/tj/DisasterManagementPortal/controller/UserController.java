package com.example.tj.DisasterManagementPortal.controller;

import com.example.tj.DisasterManagementPortal.model.UserSignupRequest;
import com.example.tj.DisasterManagementPortal.model.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.tj.DisasterManagementPortal.model.User;
import com.example.tj.DisasterManagementPortal.service.UserService;
import com.example.tj.DisasterManagementPortal.service.GeocodingService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.tj.DisasterManagementPortal.model.User.Role.VOLUNTEER;

@CrossOrigin(origins = "http://localhost:3000") // Allow frontend requests
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    //function to get the user's coordinates
    @GetMapping("/{id}/coordinates")
    public ResponseEntity<?> getUserCoordinates(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String address = user.getCity() + ", " + user.getCountry();
        try {
            GeocodingService.Coordinates coordinates = geocodingService.getCoordinates(address);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get coordinates", e);
        }
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        System.out.println("User created successfully with ID: " + createdUser.getId());
        return createdUser;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserSignupRequest userSignupRequest) {

        // Validate password strength using the service method
        if (!userService.isValidPassword(userSignupRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 8 characters long and include at least one number and one special character.");
        }
        
        // Validate that the passwords match
        if (!userSignupRequest.getPassword().equals(userSignupRequest.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }

        // Check if the email is already taken
        if (userService.isEmailTaken(userSignupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already in use");
        }

        // Create user object
        User newUser = new User();
        newUser.setEmail(userSignupRequest.getEmail());
        newUser.setPassword(userSignupRequest.getPassword());
        newUser.setName(userSignupRequest.getName());
        newUser.setCity(userSignupRequest.getCity());
        newUser.setCountry(userSignupRequest.getCountry());
        newUser.setRole(VOLUNTEER);

        userService.createUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        existingUser.setName(user.getName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setCity(user.getCity());
        existingUser.setCountry(user.getCountry());
        existingUser.setBio(user.getBio());

        if (user.getRole() == User.Role.ORG) {
            existingUser.setMission(user.getMission());
            existingUser.setWebsite(user.getWebsite());
        }

        User updatedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
