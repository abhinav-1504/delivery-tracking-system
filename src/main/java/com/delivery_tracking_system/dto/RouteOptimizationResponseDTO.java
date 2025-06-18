package com.delivery_tracking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteOptimizationResponseDTO {
    private List<OrderDTO> optimizedRoute;
}
