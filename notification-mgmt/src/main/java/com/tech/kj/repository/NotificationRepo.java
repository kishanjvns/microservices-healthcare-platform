package com.tech.kj.repository;

import com.tech.kj.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepo extends JpaRepository<NotificationEntity, UUID> {

    /**
     * Check if notification with this outbox event ID already exists.
     * Used for idempotency check to prevent duplicate processing.
     *
     * @param outboxEventId The outbox event ID from Debezium
     * @return true if notification already processed
     */
    boolean existsByOutboxEventId(String outboxEventId);

    /**
     * Find notifications by appointment ID.
     * Used for manual retry lookup and status checking.
     *
     * @param appointmentId The appointment ID
     * @return List of notifications for this appointment
     */
    @Query("SELECT n FROM NotificationEntity n " +
           "LEFT JOIN FETCH n.deliveryStatus " +
           "WHERE n.appointmentId = :appointmentId " +
           "ORDER BY n.deliveryStatus.createdAt DESC")
    List<NotificationEntity> findByAppointmentId(@Param("appointmentId") String appointmentId);
}
