package com.example.tj.DisasterManagementPortal.model;

import jakarta.persistence.*;

@Entity
public class User {

    public enum Role {
        ADMIN, ORG, VOLUNTEER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String city;  // New field for city

    @Column(nullable = true)
    private String country;  // New field for country

    @Column(nullable = true)
    private String bio;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = true)
    private String mission;

    @Column(nullable = true)
    private String website;

    @Column(nullable = false)
    private int completedHours = 0;

    @Column(nullable = true)
    private String badge = "None";

    public User() {}

    public User(Long id, String name, String email, String password, Role role, String phoneNumber, String city, String country, String bio, String mission, String website, String badge) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.country = country;
        this.bio = bio;
        this.mission = mission;
        this.website = website;
        this.badge = badge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getCompletedHours() { return completedHours; }

    public void setCompletedHours(int completedHours) { this.completedHours = completedHours; }

    public void addCompletedHours(int hours) {
        this.completedHours += hours; System.out.println("hours added");
        if (0 <= this.completedHours && this.completedHours < 20) {
            this.setBadge("None");
        } else if (20 <= this.completedHours && this.completedHours < 40) {
            this.setBadge("Bronze");
        } else if (40 <= this.completedHours && this.completedHours < 60) {
            this.setBadge("Silver");
        } else if (60 <= this.completedHours && this.completedHours < 80) {
            this.setBadge("Gold");
        } else if (80 <= this.completedHours && this.completedHours < 100) {
            this.setBadge("Platinum");
        } else if (100 <= this.completedHours) {
            this.setBadge("Legendary");
        }
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

}
