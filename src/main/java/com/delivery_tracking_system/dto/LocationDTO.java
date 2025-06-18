package com.delivery_tracking_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationDTO {
    @NotNull(message = "Delivery Agent ID is mandatory")
    private Long deliveryAgentId;
    @NotNull(message = "latitude is mandatory")
    private Double latitude;
    @NotNull(message = "Longitude is mandatory")
    private Double longitude;
}
