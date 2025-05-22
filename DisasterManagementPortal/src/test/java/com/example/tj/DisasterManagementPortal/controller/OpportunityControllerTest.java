package com.example.tj.DisasterManagementPortal.controller;

import com.example.tj.DisasterManagementPortal.model.Opportunity;
import com.example.tj.DisasterManagementPortal.service.OpportunityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OpportunityControllerTest {

    @Mock
    private OpportunityService opportunityService;

    @InjectMocks
    private OpportunityController opportunityController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(opportunityController).build();
    }

    // Test: Get all opportunities
    @Test
    void testGetAllOpportunities() throws Exception {
        Opportunity opp1 = new Opportunity();
        Opportunity opp2 = new Opportunity();
        when(opportunityService.getAllOpportunities()).thenReturn(Arrays.asList(opp1, opp2));

        mockMvc.perform(get("/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // Test: Get opportunity by ID - Success
    @Test
    void testGetOpportunityByIdSuccess() throws Exception {
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        when(opportunityService.getOpportunityById(1L)).thenReturn(Optional.of(opportunity));

        mockMvc.perform(get("/opportunities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // Test: Get opportunity by ID - Not Found
    @Test
    void testGetOpportunityByIdNotFound() throws Exception {
        when(opportunityService.getOpportunityById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/opportunity/1"))
                .andExpect(status().isNotFound());
    }

    // Test: Get opportunities by organization ID
    @Test
    void testGetOpportunitiesByOrganizationId() throws Exception {
        Opportunity opp = new Opportunity();
        when(opportunityService.getOpportunitiesByOrg(1L)).thenReturn(List.of(opp));

        mockMvc.perform(get("/opportunities/organization/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // Test: Create opportunity
    @Test
    void testCreateOpportunity() throws Exception {
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle("Test Opportunity");

        when(opportunityService.createOpportunity(any(Opportunity.class))).thenReturn(opportunity);

        mockMvc.perform(post("/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(opportunity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Opportunity"));
    }

    // Test: Update opportunity - Success
    @Test
    void testUpdateOpportunitySuccess() throws Exception {
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Updated");

        when(opportunityService.updateOpportunity(eq(1L), any(Opportunity.class))).thenReturn(opportunity);

        mockMvc.perform(put("/opportunities/1")  // ← FIXED: was "/opportunity/1"
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(opportunity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }


    // Test: Update opportunity - Not Found
    @Test
    void testUpdateOpportunityNotFound() throws Exception {
        Opportunity opportunity = new Opportunity();

        when(opportunityService.updateOpportunity(eq(1L), any(Opportunity.class))).thenReturn(opportunity);

        mockMvc.perform(put("/opportunity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(opportunity)))
                .andExpect(status().isNotFound());
    }

    // Test: Delete opportunity - Success
    @Test
    void testDeleteOpportunitySuccess() throws Exception {
        doNothing().when(opportunityService).deleteOpportunity(1L);

        mockMvc.perform(delete("/opportunities/1"))
                .andExpect(status().isOk()); // expect 200 instead of 204
    }

    // Test: Delete opportunity - Not Found
    @Test
    void testDeleteOpportunityNotFound() throws Exception {
        doThrow(new RuntimeException("Opportunity not found")).when(opportunityService).deleteOpportunity(1L);

        mockMvc.perform(delete("/opportunity/1"))
                .andExpect(status().isNotFound());
    }

    // Test: Get All Opportunities - No Opportunities
    @Test
    void testGetAllOpportunitiesEmpty() throws Exception {
        when(opportunityService.getAllOpportunities()).thenReturn(List.of());

        mockMvc.perform(get("/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}
