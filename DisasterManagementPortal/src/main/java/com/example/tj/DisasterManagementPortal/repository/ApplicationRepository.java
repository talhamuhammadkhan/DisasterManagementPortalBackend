package com.example.tj.DisasterManagementPortal.repository;

import com.example.tj.DisasterManagementPortal.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByVolunteerId(Long volunteerId);
    List<Application> findByOpportunityId(Long opportunityId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Application a WHERE a.volunteer.id = :volunteerId")
    void deleteByVolunteerId(Long volunteerId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.opportunity.id = :opportunityId AND a.status = 'COMPLETED' AND a.completedHours IS NOT NULL")
    Long countCompletedVolunteersByOpportunity(@Param("opportunityId") Long opportunityId);

}
