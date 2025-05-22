package com.example.tj.DisasterManagementPortal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private User organization;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private double distance;

    @Column(nullable = true)
    private Double lat;

    @Column(nullable = true)
    private Double lng;

    @Column(nullable = false)
    private Integer requiredVolunteers;

    @Column(nullable = false)
    private Integer completedCount = 0;

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Application> applications;

    public Opportunity() {}

    public Opportunity(User organization, String title, String description, String date, String time,
                       String streetAddress, String city, String state, String country,
                       Integer requiredVolunteers, List<Application> applications) {
        this.organization = organization;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.country = country;
        this.requiredVolunteers = requiredVolunteers;
        this.applications = applications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOrganization() {
        return organization;
    }

    public void setOrganization(User organization) {
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getRequiredVolunteers() {
        return requiredVolunteers;
    }

    public void setRequiredVolunteers(Integer requiredVolunteers) {
        this.requiredVolunteers = requiredVolunteers;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
        updateCompletedCount();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getCompletedCount() { return completedCount; }

    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }

    public void updateCompletedCount() {
        if (applications != null) {
            this.completedCount = (int) applications.stream()
                    .filter(app -> app.getStatus() == Application.Status.COMPLETED)
                    .count();
        } else {
            this.completedCount = 0;
        }
    }

}
