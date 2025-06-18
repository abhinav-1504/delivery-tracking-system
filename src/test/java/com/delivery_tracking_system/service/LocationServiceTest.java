package com.delivery_tracking_system.service;

import com.delivery_tracking_system.dto.LocationDTO;
import com.delivery_tracking_system.entity.Location;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.exception.ResourceNotFoundException;
import com.delivery_tracking_system.repository.LocationRepository;
import com.delivery_tracking_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LocationService locationService;

    private User agent;
    private LocationDTO locationDTO;

    @BeforeEach
    public void setUp() {
        agent = new User();
        agent.setId(1L);
        agent.setRole("AGENT");
        locationDTO = new LocationDTO();
        locationDTO.setDeliveryAgentId(1L);
        locationDTO.setLatitude(12.9716);
        locationDTO.setLongitude(77.5946);
    }

    @Test
    public void testUpdateLocationSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(agent));
        when(locationRepository.save(any(Location.class))).thenReturn(new Location());

        Location location = locationService.updateLocation(locationDTO);
        assertNotNull(location);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    public void testUpdateLocationAgentNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> locationService.updateLocation(locationDTO));
    }
}
