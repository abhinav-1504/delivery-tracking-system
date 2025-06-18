package com.delivery_tracking_system.controller;

import com.delivery_tracking_system.dto.LocationDTO;
import com.delivery_tracking_system.dto.RouteOptimizationResponseDTO;
import com.delivery_tracking_system.entity.Location;
import com.delivery_tracking_system.service.LocationService;
import com.delivery_tracking_system.service.RouteOptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class DeliveryAgentController {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryAgentController.class);
    private final LocationService locationService;
    private final RouteOptimizationService routeOptimizationService;

    @PostMapping("/location")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Update agent location", description = "Delivery agents can update their location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Agent not found")
    })
    public Location updateLocation(@Valid @RequestBody LocationDTO locationDTO) {
        logger.info("Updating location for agent ID :{}", locationDTO.getDeliveryAgentId());
        return locationService.updateLocation(locationDTO);
    }

    @GetMapping("/optimize-route/{deliveryAgentId}")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Optimize delivery route", description = "Delivery agents can get an optimized route for pending orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route optimized successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Agent or location not found")
    })
    public RouteOptimizationResponseDTO optimizedRoute(@PathVariable Long deliveryAgentId) {
        logger.info("Requesting route optimization for agent ID: {}", deliveryAgentId);
        return routeOptimizationService.optimizeRoute(deliveryAgentId);
    }
}
