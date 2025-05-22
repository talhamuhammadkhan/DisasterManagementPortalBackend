package com.example.tj.DisasterManagementPortal.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.tj.DisasterManagementPortal.model.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    List<Opportunity> findByOrganizationId(Long orgId);

    /*@Query("SELECT o FROM Opportunity o WHERE " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(o.latitude)) * " +
            "cos(radians(o.longitude) - radians(:lon)) + " +
            "sin(radians(:lat)) * sin(radians(o.latitude)))) < 50") // Finds opportunities within 50km
    List<Opportunity> findByDistance(@Param("lat") double lat, @Param("lon") double lon);*/
}
