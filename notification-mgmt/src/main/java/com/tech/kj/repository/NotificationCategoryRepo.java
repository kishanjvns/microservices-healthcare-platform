package com.tech.kj.repository;

import com.tech.kj.constants.NotificationType;
import com.tech.kj.entity.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationCategoryRepo extends JpaRepository<NotificationCategory, UUID> {

   Optional<NotificationCategory> findByCategoryName(String categoryName);
}
