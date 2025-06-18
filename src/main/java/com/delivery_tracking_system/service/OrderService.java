package com.delivery_tracking_system.service;

import com.delivery_tracking_system.dto.OrderDTO;
import com.delivery_tracking_system.entity.Order;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.exception.ResourceNotFoundException;
import com.delivery_tracking_system.repository.OrderRepository;
import com.delivery_tracking_system.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
     private final NotificationService notificationService;

    public Order createOrder(@Valid OrderDTO orderDTO) {
        logger.info("Creating order for customer ID : {}", orderDTO.getCustomerId());
        Order order = new Order();
        order.setStatus(orderDTO.getStatus());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());

        User customer = userRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + orderDTO.getCustomerId()));
        order.setCustomer(customer);

        if(orderDTO.getDeliveryAgentId() != null) {
            User deliveryAgent = userRepository.findById(orderDTO.getDeliveryAgentId()).
                    orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with ID: " + orderDTO.getDeliveryAgentId()));
            order.setDeliveryAgent(deliveryAgent);
        }

        Order savedOrder = orderRepository.save(order);
         notificationService.sendOrderStatusEmail(customer.getEmail(), savedOrder);
        logger.info("Order created with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    public Order updateOrderStatus(Long orderId, String status) {
        logger.info("Updating status for order ID: {} to {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
         notificationService.sendOrderStatusEmail(order.getCustomer().getEmail(), updatedOrder);
        logger.info("Order status updated for order ID : {}", orderId);
        return updatedOrder;
    }

    public List<Order> getOrderHistory(Long customerID) {
        logger.info("fetching order history for customer ID : {}", customerID);
        return orderRepository.findAll().stream()
                .filter(order -> order.getCustomer().getId().equals(customerID))
                .toList();
    }
}
