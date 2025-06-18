package com.delivery_tracking_system.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_table")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;
    @ManyToOne
    @JoinColumn(name = "delivery_agent_id")
    private User deliveryAgent;
    private String deliveryAddress;
}
