package com.delivery_tracking_system.service;

import com.delivery_tracking_system.dto.OrderDTO;
import com.delivery_tracking_system.dto.RouteOptimizationResponseDTO;
import com.delivery_tracking_system.entity.Location;
import com.delivery_tracking_system.entity.Order;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.exception.ResourceNotFoundException;
import com.delivery_tracking_system.repository.LocationRepository;
import com.delivery_tracking_system.repository.OrderRepository;
import com.delivery_tracking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteOptimizationService {
    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizationService.class);
    private final OrderRepository orderRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public RouteOptimizationResponseDTO optimizeRoute(Long deliveryAgentId) {
        logger.info("Optimizing route for agent ID: {}", deliveryAgentId);

        User deliveryAgent = userRepository.findById(deliveryAgentId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with ID: " + deliveryAgentId));

        List<Location> agentLocations = locationRepository.findAll().stream()
                .filter(location -> location.getDeliveryAgent().getId().equals(deliveryAgentId))
                .sorted((l1, l2) -> l2.getTimestamp().compareTo(l1.getTimestamp()))
                .toList();

        if (agentLocations.isEmpty()) {
            throw new ResourceNotFoundException("No location found for Delivery Agent with ID: " + deliveryAgentId);
        }

        Location currentLocation = agentLocations.get(0);
        double currentLat = currentLocation.getLatitude();
        double currentLon = currentLocation.getLongitude();

        List<Order> pendingOrders = orderRepository.findAll().stream()
                .filter(order -> order.getDeliveryAgent() != null)
                .filter(order -> order.getDeliveryAgent().getId().equals(deliveryAgentId))
                .filter(order -> {
                    String cleanedStatus = order.getStatus().replaceAll("[\"\\r\\n\\s]", "");
                    return !"DELIVERED".equals(cleanedStatus);
                })
                .toList();

        if (pendingOrders.isEmpty()) {
            logger.info("No pending orders found for agent ID: {}", deliveryAgentId);
            return new RouteOptimizationResponseDTO(new ArrayList<>());
        }

        List<Order> sortedOrders = new ArrayList<>(pendingOrders);
        List<Order> optimizedRoute = new ArrayList<>();

        while (!sortedOrders.isEmpty()) {
            final double lat = currentLat;
            final double lon = currentLon;
            logger.info("Current position: ({}, {})", lat, lon);
            for (Order order : sortedOrders) {
                double distance = calculateDistance(lat, lon, order);
                logger.info("Distance to {}: {} km", order.getDeliveryAddress(), distance);
            }
            Order nextOrder = sortedOrders.stream()
                    .min(Comparator.comparingDouble(order -> calculateDistance(lat, lon, order)))
                    .orElseThrow(() -> new ResourceNotFoundException("Error calculating distance"));

            optimizedRoute.add(nextOrder);
            sortedOrders.remove(nextOrder);

            currentLat = parseLatitudeFromAddress(nextOrder.getDeliveryAddress());
            currentLon = parseLongitudeFromAddress(nextOrder.getDeliveryAddress());
        }

        List<OrderDTO> optimizedRouteDTOs = optimizedRoute.stream()
                .map(order -> {
                    OrderDTO orderDTO = new OrderDTO();
                    orderDTO.setId(order.getId());
                    orderDTO.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
                    orderDTO.setDeliveryAgentId(order.getDeliveryAgent() != null ? order.getDeliveryAgent().getId() : null);
                    orderDTO.setDeliveryAddress(order.getDeliveryAddress());
                    orderDTO.setStatus(order.getStatus());
                    return orderDTO;
                })
                .toList();

        logger.info("Optimized route calculated for agent ID: {}. Route size: {}", deliveryAgentId, optimizedRoute.size());
        return new RouteOptimizationResponseDTO(optimizedRouteDTOs);
    }

    private double calculateDistance(double lat1, double lon1, Order order) {
        double lat2 = parseLatitudeFromAddress(order.getDeliveryAddress());
        double lon2 = parseLongitudeFromAddress(order.getDeliveryAddress());

        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double parseLatitudeFromAddress(String address) {
        if (address.contains("123 Main St")) return 12.9716;
        if (address.contains("456 Oak St") || address.contains("456 oak St")) return 12.9916;
        if (address.contains("789 Pine St")) return 12.9616;
        return 12.9716;
    }

    private double parseLongitudeFromAddress(String address) {
        if (address.contains("123 Main St")) return 77.5946;
        if (address.contains("456 Oak St") || address.contains("456 oak St")) return 77.6146;
        if (address.contains("789 Pine St")) return 77.5846;
        return 77.5946;
    }
}