package com.delivery_tracking_system.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "location")
@Data
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "delivery_agent_id")
    private User deliveryAgent;

    private double latitude;
    private double longitude;

    private LocalDateTime timestamp;
}