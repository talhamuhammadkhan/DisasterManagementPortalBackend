package com.example.tj.DisasterManagementPortal.service;

import com.example.tj.DisasterManagementPortal.model.Application;
import com.example.tj.DisasterManagementPortal.model.Opportunity;
import com.example.tj.DisasterManagementPortal.model.User;
import com.example.tj.DisasterManagementPortal.repository.ApplicationRepository;
import com.example.tj.DisasterManagementPortal.repository.OpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger logger = Logger.getLogger(ApplicationServiceImpl.class.getName());

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Override
    public Application applyForOpportunity(Application application) {
        return applicationRepository.save(application);
    }

    @Override
    public List<Application> getApplicationsByVolunteer(Long volunteerId) {
        return applicationRepository.findByVolunteerId(volunteerId);
    }

    @Override
    public List<Application> getApplicationsByOpportunity(Long opportunityId) {
        return applicationRepository.findByOpportunityId(opportunityId);
    }

    @Override
    public Application updateApplicationStatus(Long applicationId, Application.Status status) {
        Optional<Application> optionalApplication = applicationRepository.findById(applicationId);

        if (optionalApplication.isEmpty()) {
            logger.warning("Application not found with ID: " + applicationId);
            return null;
        }

        Application application = optionalApplication.get();
        Opportunity opportunity = application.getOpportunity();

        application.setStatus(status);
        logger.info("Application ID " + applicationId + " status updated to: " + status);
        opportunity.getApplications().add(application);
        opportunity.updateCompletedCount();
        return applicationRepository.save(application);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id).orElse(null);
    }

    @Transactional
    public Application updateApplication(Application application) {
        return applicationRepository.save(application);
    }
}
