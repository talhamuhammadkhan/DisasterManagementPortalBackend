package com.example.tj.DisasterManagementPortal.service;

import com.example.tj.DisasterManagementPortal.model.Opportunity;
import com.example.tj.DisasterManagementPortal.repository.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OpportunityServiceImpl implements OpportunityService {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Override
    public Opportunity createOpportunity(Opportunity opportunity) {
        return opportunityRepository.save(opportunity);
    }

    @Override
    public List<Opportunity> getAllOpportunities() {
        return opportunityRepository.findAll();
    }

    @Override
    public List<Opportunity> getOpportunitiesByOrg(Long orgId) {
        return opportunityRepository.findByOrganizationId(orgId);
    }

    @Override
    public void deleteOpportunity(Long id) {
        opportunityRepository.deleteById(id);
    }

    public Opportunity updateOpportunity(Long id, Opportunity opportunityDetails) {
        // Check if opportunity exists
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found with id " + id));

        // Update the opportunity fields
        opportunity.setTitle(opportunityDetails.getTitle());
        opportunity.setDescription(opportunityDetails.getDescription());
        opportunity.setDate(opportunityDetails.getDate());
        opportunity.setTime(opportunityDetails.getTime());
        opportunity.setStreetAddress(opportunityDetails.getStreetAddress());
        opportunity.setCity(opportunityDetails.getCity());
        opportunity.setState(opportunityDetails.getState());
        opportunity.setCountry(opportunityDetails.getCountry());
        opportunity.setRequiredVolunteers(opportunityDetails.getRequiredVolunteers());

        // Save the updated opportunity
        return opportunityRepository.save(opportunity);
    }

    @Override
    public Optional<Opportunity> getOpportunityById(Long id) {
        return opportunityRepository.findById(id);
    }

}
