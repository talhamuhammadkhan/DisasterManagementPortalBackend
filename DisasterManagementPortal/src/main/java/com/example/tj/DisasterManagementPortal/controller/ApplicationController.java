package com.example.tj.DisasterManagementPortal.controller;

import com.example.tj.DisasterManagementPortal.model.Application;
import com.example.tj.DisasterManagementPortal.model.Opportunity;
import com.example.tj.DisasterManagementPortal.model.User;
import com.example.tj.DisasterManagementPortal.model.Message;
import com.example.tj.DisasterManagementPortal.repository.ApplicationRepository;
import com.example.tj.DisasterManagementPortal.repository.OpportunityRepository;
import com.example.tj.DisasterManagementPortal.repository.UserRepository;
import com.example.tj.DisasterManagementPortal.service.ApplicationService;
import com.example.tj.DisasterManagementPortal.service.MessageService;
import com.example.tj.DisasterManagementPortal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static java.sql.Types.NULL;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private static final Logger logger = Logger.getLogger(ApplicationController.class.getName());

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MessageService messageService;

    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(application);
    }

    @PostMapping()
    public ResponseEntity<Application> applyForOpportunity(@RequestBody Application applicationData) {
        Optional<Opportunity> opportunityOpt = opportunityRepository.findById(applicationData.getOpportunity().getId());
        Optional<User> volunteerOpt = userRepository.findById(applicationData.getVolunteer().getId());

        if (opportunityOpt.isEmpty()) {
            logger.warning("Attempt to apply for a non-existent opportunity.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if (volunteerOpt.isEmpty()) {
            logger.warning("Attempt to apply with a non-existent volunteer.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        applicationData.setOpportunity(opportunityOpt.get());
        applicationData.setVolunteer(volunteerOpt.get());

        Application savedApplication = applicationService.applyForOpportunity(applicationData);
        logger.info("Application submitted by Volunteer ID: " + applicationData.getVolunteer().getId());
        return ResponseEntity.ok(savedApplication);
    }

    @GetMapping("/opportunity/{opportunityId}")
    public ResponseEntity<List<Application>> getApplicationsByOpportunity(@PathVariable Long opportunityId) {
        List<Application> applications = applicationService.getApplicationsByOpportunity(opportunityId);

        return applications.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(applications);
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<List<Application>> getApplicationsByVolunteer(@PathVariable Long volunteerId) {
        List<Application> applications = applicationService.getApplicationsByVolunteer(volunteerId);

        return applications.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(applications);
    }


    @PutMapping("/{applicationId}/{status}")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @PathVariable String status // Taking status from the path
    ) {
        try {
            // Convert the status string to the Status enum
            Application.Status applicationStatus = Application.Status.valueOf(status.toUpperCase());

            Application updatedApplication = applicationService.updateApplicationStatus(applicationId, applicationStatus);
            return updatedApplication != null ? ResponseEntity.ok(updatedApplication) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        } catch (SecurityException e) {
            logger.warning(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PatchMapping("/{applicationId}/log-hours")
    public ResponseEntity<?> logHoursForOpportunity(
            @PathVariable Long applicationId,
            @RequestParam Integer hoursLogged,
            @RequestParam String feedback) {

        if (hoursLogged == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hours logged is required.");
        }

        Application application = applicationService.getApplicationById(applicationId);
        if (application == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found.");
        }

        if (application.getStatus() != Application.Status.COMPLETED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hours can only be logged for completed applications.");
        }

        User volunteer = application.getVolunteer();
        volunteer.addCompletedHours(hoursLogged);
        userService.updateUser(volunteer);

        application.setCompletedHours(hoursLogged);
        application.setFeedback(feedback);
        applicationService.updateApplication(application);

        return ResponseEntity.status(HttpStatus.OK).body("Hours and feedback logged successfully.");
    }

    @PutMapping("/top-volunteer/{id}")
    public ResponseEntity<String> markTopVolunteer(@PathVariable Long id) {
        Optional<Application> optionalApplication = applicationRepository.findById(id);

        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            application.setIsTopVolunteer(!application.isTopVolunteer());
            applicationRepository.save(application);
            return ResponseEntity.ok("Top volunteer status updated.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application not found.");
    }

    @PostMapping("/{applicationId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> messagePayload) {

        String content = messagePayload.get("content");
        String senderRole = messagePayload.get("senderRole");

        if (content == null || senderRole == null) {
            return ResponseEntity.badRequest().build();
        }

        Application application = applicationService.getApplicationById(applicationId);
        if (application == null) {
            return ResponseEntity.notFound().build();
        }

        Message message = new Message();
        message.setApplication(application);
        message.setContent(content);
        message.setSenderRole(senderRole.toUpperCase());
        message.setUnread(true);
        message.setTimestamp(java.time.LocalDateTime.now());
        application.addMessage(message);

        return ResponseEntity.ok(messageService.sendMessage(message));
    }

    @GetMapping("/{applicationId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long applicationId) {
        List<Message> messages = messageService.getMessagesForApplication(applicationId);


        return ResponseEntity.ok(messages);
    }

}
