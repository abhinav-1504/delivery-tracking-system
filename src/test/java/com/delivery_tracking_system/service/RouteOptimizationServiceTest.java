package com.delivery_tracking_system.service;

import com.delivery_tracking_system.dto.RouteOptimizationResponseDTO;
import com.delivery_tracking_system.entity.Location;
import com.delivery_tracking_system.entity.Order;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.exception.ResourceNotFoundException;
import com.delivery_tracking_system.repository.LocationRepository;
import com.delivery_tracking_system.repository.OrderRepository;
import com.delivery_tracking_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouteOptimizationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RouteOptimizationService routeOptimizationService;

    private User agent;
    private User customer;
    private Location location;
    private Order order1;
    private Order order2;

    @BeforeEach
    public void setUp() {
        agent = new User();
        agent.setId(1L);

        customer = new User();
        customer.setId(2L);

        location = new Location();
        location.setDeliveryAgent(agent);
        location.setLatitude(12.9716);
        location.setLongitude(77.5946);
        location.setTimestamp(LocalDateTime.parse("2025-06-16T10:00:00"));

        order1 = new Order();
        order1.setId(1L);
        order1.setDeliveryAgent(agent);
        order1.setCustomer(customer);
        order1.setStatus("PENDING");
        order1.setDeliveryAddress("456 oak St");

        order2 = new Order();
        order2.setId(2L);
        order2.setDeliveryAgent(agent);
        order2.setCustomer(customer);
        order2.setStatus("PENDING");
        order2.setDeliveryAddress("789 Pine St");
    }

    @Test
    public void testOptimizeRouteSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(agent));
        when(locationRepository.findAll()).thenReturn(Arrays.asList(location));
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1));

        RouteOptimizationResponseDTO response = routeOptimizationService.optimizeRoute(1L);

        assertNotNull(response);
        assertEquals(1, response.getOptimizedRoute().size());
        assertEquals("456 oak St", response.getOptimizedRoute().get(0).getDeliveryAddress());
    }

    @Test
    public void testOptimizeRouteWithMultipleOrders() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(agent));
        when(locationRepository.findAll()).thenReturn(Arrays.asList(location));
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order2, order1));

        RouteOptimizationResponseDTO response = routeOptimizationService.optimizeRoute(1L);

        assertNotNull(response);
        assertEquals(2, response.getOptimizedRoute().size());

        assertEquals("789 Pine St", response.getOptimizedRoute().get(0).getDeliveryAddress());
        assertEquals("456 oak St", response.getOptimizedRoute().get(1).getDeliveryAddress()); // Match lowercase
    }

    @Test
    public void testOptimizeRouteNoPendingOrders() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(agent));
        when(locationRepository.findAll()).thenReturn(Arrays.asList(location));
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        RouteOptimizationResponseDTO response = routeOptimizationService.optimizeRoute(1L);

        assertNotNull(response);
        assertTrue(response.getOptimizedRoute().isEmpty());
    }

    @Test
    public void testOptimizeRouteNoLocation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(agent));
        when(locationRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> routeOptimizationService.optimizeRoute(1L));
    }
}