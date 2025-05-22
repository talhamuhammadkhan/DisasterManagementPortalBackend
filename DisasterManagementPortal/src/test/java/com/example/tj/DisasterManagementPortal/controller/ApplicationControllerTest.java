package com.example.tj.DisasterManagementPortal.controller;

import com.example.tj.DisasterManagementPortal.model.*;
import com.example.tj.DisasterManagementPortal.repository.ApplicationRepository;
import com.example.tj.DisasterManagementPortal.repository.OpportunityRepository;
import com.example.tj.DisasterManagementPortal.repository.UserRepository;
import com.example.tj.DisasterManagementPortal.service.ApplicationService;
import com.example.tj.DisasterManagementPortal.service.MessageService;
import com.example.tj.DisasterManagementPortal.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private UserService userService;

    @MockBean
    private OpportunityRepository opportunityRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ApplicationRepository applicationRepository;

    @MockBean
    private MessageService messageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Test successful application to an opportunity
    @Test
    void testApplyForOpportunity_Success() throws Exception {
        Application application = new Application();
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        User volunteer = new User();
        volunteer.setId(1L);
        application.setOpportunity(opportunity);
        application.setVolunteer(volunteer);

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(applicationService.applyForOpportunity(any(Application.class))).thenReturn(application);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isOk());
    }

    // Test applying for a non-existent opportunity
    @Test
    void testApplyForOpportunity_InvalidOpportunity() throws Exception {
        Application application = new Application();
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        application.setOpportunity(opportunity);

        // Set a dummy volunteer to avoid NullPointerException
        User volunteer = new User();
        volunteer.setId(1L);
        application.setVolunteer(volunteer);

        when(opportunityRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isBadRequest());
    }


    // Test getting applications by opportunity ID
    @Test
    void testGetApplicationsByOpportunity() throws Exception {
        when(applicationService.getApplicationsByOpportunity(1L)).thenReturn(List.of(new Application()));

        mockMvc.perform(get("/applications/opportunity/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Test getting applications for a volunteer that has none
    @Test
    void testGetApplicationsByVolunteer_NoContent() throws Exception {
        when(applicationService.getApplicationsByVolunteer(99L)).thenReturn(List.of());

        mockMvc.perform(get("/applications/volunteer/99"))
                .andExpect(status().isNoContent());
    }

    // Test updating an application's status successfully
    @Test
    void testUpdateApplicationStatus_Success() throws Exception {
        Application app = new Application();
        app.setId(1L);
        app.setStatus(Application.Status.APPROVED);

        when(applicationService.updateApplicationStatus(1L, Application.Status.APPROVED)).thenReturn(app);

        mockMvc.perform(put("/applications/1/APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    // Test updating application status with invalid enum
    @Test
    void testUpdateApplicationStatus_InvalidStatus() throws Exception {
        mockMvc.perform(put("/applications/1/INVALID"))
                .andExpect(status().isBadRequest());
    }

    // Test logging hours for a completed application
    @Test
    void testLogHoursForCompletedApplication() throws Exception {
        Application app = new Application();
        app.setId(1L);
        app.setStatus(Application.Status.COMPLETED);
        User volunteer = new User();
        volunteer.setId(1L);
        app.setVolunteer(volunteer);

        when(applicationService.getApplicationById(1L)).thenReturn(app);

        mockMvc.perform(patch("/applications/1/log-hours")
                        .param("hoursLogged", "5")
                        .param("feedback", "Great job"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hours and feedback logged successfully")));
    }

    // Test logging hours for an application that is not completed
    @Test
    void testLogHoursForIncompleteApplication() throws Exception {
        Application app = new Application();
        app.setId(1L);
        app.setStatus(Application.Status.PENDING);

        when(applicationService.getApplicationById(1L)).thenReturn(app);

        mockMvc.perform(patch("/applications/1/log-hours")
                        .param("hoursLogged", "5")
                        .param("feedback", "Not done"))
                .andExpect(status().isForbidden());
    }

    // Test sending a message successfully for an application
    @Test
    void testSendMessage_Success() throws Exception {
        Application app = new Application();
        app.setId(1L);
        Message msg = new Message();
        msg.setContent("Hello");
        msg.setSenderRole("VOLUNTEER");

        when(applicationService.getApplicationById(1L)).thenReturn(app);
        when(messageService.sendMessage(any(Message.class))).thenReturn(msg);

        Map<String, String> payload = Map.of("content", "Hello", "senderRole", "VOLUNTEER");

        mockMvc.perform(post("/applications/1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    // Test sending a message to an invalid application
    @Test
    void testSendMessage_InvalidApplication() throws Exception {
        when(applicationService.getApplicationById(999L)).thenReturn(null);

        Map<String, String> payload = Map.of("content", "Hello", "senderRole", "VOLUNTEER");

        mockMvc.perform(post("/applications/999/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    // Test retrieving all messages for a given application
    @Test
    void testGetMessages() throws Exception {
        Message msg = new Message();
        msg.setContent("Hi");

        when(messageService.getMessagesForApplication(1L)).thenReturn(List.of(msg));

        mockMvc.perform(get("/applications/1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Test retrieving an application by ID successfully
    @Test
    void testGetApplicationById_Success() throws Exception {
        Application app = new Application();
        app.setId(5L);

        when(applicationService.getApplicationById(5L)).thenReturn(app);

        mockMvc.perform(get("/applications/5"))
                .andExpect(status().isOk());
    }
}
