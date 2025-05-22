package com.example.tj.DisasterManagementPortal.service;

import com.example.tj.DisasterManagementPortal.model.Application;
import java.util.List;

public interface ApplicationService {
    Application applyForOpportunity(Application application);
    List<Application> getApplicationsByVolunteer(Long volunteerId);
    List<Application> getApplicationsByOpportunity(Long opportunityId);
    Application updateApplicationStatus(Long applicationId, Application.Status status);
    Application updateApplication(Application application);
    Application getApplicationById(Long applicationId);
}

