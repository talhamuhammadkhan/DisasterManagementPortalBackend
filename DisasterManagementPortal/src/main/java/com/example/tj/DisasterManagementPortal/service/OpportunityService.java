package com.example.tj.DisasterManagementPortal.service;

import com.example.tj.DisasterManagementPortal.model.Opportunity;
import java.util.List;
import java.util.Optional;

public interface OpportunityService {
    Opportunity createOpportunity(Opportunity opportunity);
    List<Opportunity> getAllOpportunities();
    List<Opportunity> getOpportunitiesByOrg(Long orgId);
    void deleteOpportunity(Long id);
    Opportunity updateOpportunity(Long id, Opportunity opportunity);
    Optional<Opportunity> getOpportunityById(Long id);
}
