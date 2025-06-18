package com.delivery_tracking_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderDTO {
    private Long id;
    @NotBlank(message = "Status is mandatory")
    private String status;
    @NotBlank(message = "Customer ID is mandatory")
    private Long customerId;
    private Long deliveryAgentId;
    @NotBlank(message = "Delivery address is mandatory")
    private String deliveryAddress;
}
