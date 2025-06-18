package com.delivery_tracking_system.controller;

import com.delivery_tracking_system.dto.OrderDTO;
import com.delivery_tracking_system.entity.Order;
import com.delivery_tracking_system.service.OrderService;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final OrderService orderService;

    @PostMapping("/order")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new order", description = "Admins can create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Customer or agent not found")
    })
    public Order createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        logger.info("Creating order for customer ID : {}", orderDTO.getCustomerId());
        return orderService.createOrder(orderDTO);
    }

    @PutMapping("/order/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Admins can update the status of an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public Order updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody String status) {
        logger.info("Updating status for order ID : {}", orderId);
        String sanitizedStatus = status.replaceAll("[\"\\r\\n\\s]", "");
        return orderService.updateOrderStatus(orderId, sanitizedStatus);
    }

}

