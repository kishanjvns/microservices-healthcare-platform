package com.tech.kj.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import com.tech.kj.constants.DeliveryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_delivery")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDelivery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false, unique = true)
    private NotificationEntity notificationEntity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status;
    
    @Builder.Default
    @Column(name = "retry")
    private int retry = 0;

    @Builder.Default
    @Column(name = "max_retries")
    private int maxRetries = 3;
    
    @Column(name = "vendor_status")
    private String vendorStatus;
    
    @Column(name = "vendor_response", columnDefinition = "TEXT")
    private String vendorResponse;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "error_code")
    private String errorCode;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastModifiedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }
}
