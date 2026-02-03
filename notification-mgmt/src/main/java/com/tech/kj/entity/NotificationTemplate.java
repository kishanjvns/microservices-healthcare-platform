package com.tech.kj.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tech.kj.constants.ChannelType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notification_template")
@Data
public class NotificationTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_category_id", nullable = false)
    private NotificationCategory notificationCategory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private ChannelType channelType;
    
    @Column(name = "template_content", columnDefinition = "TEXT", nullable = false)
    private String templateContent;
    
    @Column(name = "subject")
    private String subject;  // For EMAIL only
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "version")
    private Integer version = 1;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
}