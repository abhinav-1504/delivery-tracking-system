package com.delivery_tracking_system.service;

import com.delivery_tracking_system.dto.OrderDTO;
import com.delivery_tracking_system.entity.Order;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.exception.ResourceNotFoundException;
import com.delivery_tracking_system.repository.OrderRepository;
import com.delivery_tracking_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private User customer;
    private User agent;
    private OrderDTO orderDTO;

    @BeforeEach
    public void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@example.com");

        agent = new User();
        agent.setId(2L);

        orderDTO = new OrderDTO();
        orderDTO.setStatus("PENDING");
        orderDTO.setCustomerId(1L);
        orderDTO.setDeliveryAgentId(2L);
        orderDTO.setDeliveryAddress("123 Main St");
    }

    @Test
    public void testCreateOrderSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(agent));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        Order order = orderService.createOrder(orderDTO);

        assertNotNull(order);
        verify(notificationService, times(1)).sendOrderStatusEmail(anyString(), any(Order.class));
    }

    @Test
    public void testCreateOrderCustomerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderDTO));
    }

    @Test
    public void testUpdateOrderStatusSuccess() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrderStatus(1L, "DELIVERED");

        assertNotNull(updatedOrder);
        assertEquals("DELIVERED", updatedOrder.getStatus());
        verify(notificationService, times(1)).sendOrderStatusEmail(anyString(), any(Order.class));
    }

    @Test
    public void testGetOrderHistory() {
        Order order = new Order();
        order.setCustomer(customer);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        List<Order> history = orderService.getOrderHistory(1L);

        assertEquals(1, history.size());
    }
}
