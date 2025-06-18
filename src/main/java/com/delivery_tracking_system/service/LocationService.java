package com.delivery_tracking_system.service;

import com.delivery_tracking_system.dto.LocationDTO;
import com.delivery_tracking_system.entity.Location;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.exception.ResourceNotFoundException;
import com.delivery_tracking_system.repository.LocationRepository;
import com.delivery_tracking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LocationService {
    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public Location updateLocation(LocationDTO locationDTO) {
        logger.info("Updating location for agent ID : {}", locationDTO.getDeliveryAgentId());
        User deliveryAgent = userRepository.findById(locationDTO.getDeliveryAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with ID : "+ locationDTO.getDeliveryAgentId()));

        Location location = new Location();
        location.setDeliveryAgent(deliveryAgent);
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        location.setTimestamp(LocalDateTime.now());

        return locationRepository.save(location);
    }

    @Scheduled(fixedRate = 30000)
    public void simulateLocationUpdates() {
        logger.info("Simulating location updates for all agents");
        Random random = new Random();
        userRepository.findAll().stream()
                .filter(user -> "AGENT".equals(user.getRole()))
                .forEach(agent -> {
                    Location location = new Location();
                    location.setDeliveryAgent(agent);
                    location.setLatitude(12.9716 + random.nextDouble() * 0.02);
                    location.setLongitude(77.5946 + random.nextDouble() * 0.02);
                    location.setTimestamp(LocalDateTime.now());
                    locationRepository.save(location);
                    logger.info("Updating location for agent ID: {}", agent.getId());
                });
    }
}
