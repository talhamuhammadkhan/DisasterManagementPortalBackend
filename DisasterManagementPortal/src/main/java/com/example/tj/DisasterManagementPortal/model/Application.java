package com.example.tj.DisasterManagementPortal.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Application {

    public enum Status {
        PENDING, APPROVED, REJECTED, COMPLETED;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private User volunteer;

    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(nullable = true)
    private Integer completedHours;

    @Column(nullable = true) // New feedback field
    private String feedback;

    @Column(nullable = false)
    private Boolean isTopVolunteer = false;

    @Column(nullable = false)
    private Integer unreadVolunteerCount = 0;

    @Column(nullable = false)
    private Integer unreadOrgCount = 0;


    public Application() {
        this.isTopVolunteer = false;
    }

    public Application(User volunteer, Opportunity opportunity, Status status, Integer completedHours, String feedback, Boolean isTopVolunteer) {
        this.volunteer = volunteer;
        this.opportunity = opportunity;
        this.status = status;
        this.completedHours = completedHours;
        this.feedback = feedback;
        this.isTopVolunteer = isTopVolunteer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(User volunteer) {
        this.volunteer = volunteer;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getCompletedHours() {
        return completedHours;
    }

    public void setCompletedHours(Integer completedHours) {
        this.completedHours = completedHours;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isTopVolunteer() {
        return isTopVolunteer;
    }

    public void setIsTopVolunteer(boolean isTopVolunteer) {
        this.isTopVolunteer = isTopVolunteer;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Integer getUnreadVolunteerCount() {
        return unreadVolunteerCount;
    }

    public void setUnreadVolunteerCount(Integer unreadVolunteerCount) {
        this.unreadVolunteerCount = unreadVolunteerCount;
    }

    public Integer getUnreadOrgCount() {
        return unreadOrgCount;
    }

    public void setUnreadOrgCount(Integer unreadOrgCount) {
        this.unreadOrgCount = unreadOrgCount;
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setApplication(this);

        if ("ORG".equalsIgnoreCase(message.getSenderRole())) {
            unreadVolunteerCount++; // Org sent message → unread for volunteer
        } else if ("VOLUNTEER".equalsIgnoreCase(message.getSenderRole())) {
            unreadOrgCount++; // Volunteer sent message → unread for org
        }
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setApplication(null);
    }

    public void markOrgMessagesAsRead() {
        this.unreadOrgCount = 0;
    }

    public void markVolunteerMessagesAsRead() {
        this.unreadVolunteerCount = 0;
    }

}
