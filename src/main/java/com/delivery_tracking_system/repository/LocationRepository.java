package com.delivery_tracking_system.repository;

import com.delivery_tracking_system.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
