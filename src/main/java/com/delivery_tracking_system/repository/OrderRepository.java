package com.delivery_tracking_system.repository;

import com.delivery_tracking_system.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDeliveryAgentIdAndStatus(Long deliveryAgentId, String status);
}
