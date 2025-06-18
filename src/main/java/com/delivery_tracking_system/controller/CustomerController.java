package com.delivery_tracking_system.controller;

import com.delivery_tracking_system.entity.Order;
import com.delivery_tracking_system.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final OrderService orderService;

    @GetMapping("/orders/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get customer orders", description = "Customers can view their orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public List<Order> getCustomerOrders(@PathVariable Long customerId) {
        logger.info("Fetching orders for customer ID :{}", customerId);
        return orderService.getOrderHistory(customerId);
    }

    @GetMapping("/orders/history/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get order history", description = "Customers can view their order history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order history retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public List<Order> gertOrderHistory(@PathVariable Long customerId) {
        logger.info("Fetching order history for customer ID :{}", customerId);
        return orderService.getOrderHistory(customerId);
    }
}
