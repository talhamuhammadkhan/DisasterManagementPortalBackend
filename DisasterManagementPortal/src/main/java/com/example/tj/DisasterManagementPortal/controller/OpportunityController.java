package com.example.tj.DisasterManagementPortal.controller;

import com.example.tj.DisasterManagementPortal.model.Opportunity;
import com.example.tj.DisasterManagementPortal.model.User;
import com.example.tj.DisasterManagementPortal.service.DistanceCalculator;
import com.example.tj.DisasterManagementPortal.service.GeocodingService;
import com.example.tj.DisasterManagementPortal.service.OpportunityService;
import com.example.tj.DisasterManagementPortal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000") // Allow frontend requests
@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    @Autowired
    private OpportunityService opportunityService;

    @Autowired
    private UserService userService;

    private GeocodingService geocodingService = new GeocodingService();

    @PostMapping
    public Opportunity createOpportunity(@RequestBody Opportunity opportunity) {
        return opportunityService.createOpportunity(opportunity);
    }

    @GetMapping
    public List<Opportunity> getAllOpportunities() {
        return opportunityService.getAllOpportunities();
    }

    @GetMapping("/{id}")
    public Opportunity getOpportunityById(@PathVariable Long id) {
        return opportunityService.getOpportunityById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));
    }

    @GetMapping("/organization/{orgId}")
    public List<Opportunity> getOpportunitiesByOrg(@PathVariable Long orgId) {
        return opportunityService.getOpportunitiesByOrg(orgId);
    }

    @DeleteMapping("/{id}")
    public void deleteOpportunity(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
    }

    @PutMapping("/{id}")
    public Opportunity updateOpportunity(@PathVariable Long id, @RequestBody Opportunity opportunity) {
        return opportunityService.updateOpportunity(id, opportunity);
    }

    @GetMapping("/closest/{volunteerId}")
    public List<Opportunity> getClosestOpportunities(@PathVariable Long volunteerId) throws Exception {
        // 1. Fetch the volunteer details
        User volunteer = userService.getUserById(volunteerId); // Assuming you have a method for this
        String volunteerAddress = volunteer.getCity() + ", " + volunteer.getCountry();

        // 2. Get coordinates of the volunteer
        GeocodingService.Coordinates volunteerCoords = geocodingService.getCoordinates(volunteerAddress);

        // 3. Fetch all opportunities
        List<Opportunity> allOpportunities = opportunityService.getAllOpportunities();

        // 4. Sort opportunities by distance
        List<Opportunity> sortedOpportunities = allOpportunities.stream()
                .map(opportunity -> {
                    String opportunityAddress = opportunity.getStreetAddress() + ", " +
                            opportunity.getCity() + ", " +
                            opportunity.getState() + ", " +
                            opportunity.getCountry();
                    try {
                        GeocodingService.Coordinates opportunityCoords = geocodingService.getCoordinates(opportunityAddress);

                        opportunity.setLat(opportunityCoords.lat);
                        opportunity.setLng(opportunityCoords.lng);

                        double distance = DistanceCalculator.calculateDistance(
                                volunteerCoords.lat,
                                volunteerCoords.lng,
                                opportunityCoords.lat,
                                opportunityCoords.lng
                        );
                        opportunity.setDistance(distance); // Add the distance to the opportunity object
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return opportunity;
                })
                .sorted((o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance())) // Sort by distance
                .collect(Collectors.toList());

        return sortedOpportunities;
    }
}
