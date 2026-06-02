package com.tech.kj.repository;

import com.tech.kj.constants.ChannelType;
import com.tech.kj.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationTemplateRepo extends JpaRepository<NotificationTemplate, UUID> {

    public Optional<NotificationTemplate> findByNotificationCategoryIdAndChannelType(UUID id, ChannelType channelType);
}
