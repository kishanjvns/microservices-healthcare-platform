package com.tech.kj.repository;

import com.tech.kj.entity.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationDeliveryRepo extends JpaRepository<NotificationDelivery, UUID> {

}
