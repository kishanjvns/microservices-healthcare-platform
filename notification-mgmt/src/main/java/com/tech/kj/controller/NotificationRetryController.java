package com.tech.kj.controller;

import com.tech.kj.exceptions.RecordNotFoundException;
import com.tech.kj.model.NotificationRetryRequest;
import com.tech.kj.model.NotificationRetryResponse;
import com.tech.kj.model.NotificationStatusResponse;
import com.tech.kj.service.NotificationRetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for querying notification status and retrying failed notifications.
 * Used by support team to monitor and retry notifications by appointment ID.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Slf4j
public class NotificationRetryController {

    @Autowired
    private NotificationRetryService notificationRetryService;

    /**
     * Get notification status for a specific appointment.
     * Shows overall status (PASS/PARTIAL_FAILURE/FAILED) and per-channel details.
     *
     * GET /api/v1/notifications/status?appointmentId=APT123
     *
     * Example Response:
     * {
     *   "appointmentId": "APT123",
     *   "overallStatus": "PARTIAL_FAILURE",
     *   "totalChannels": 3,
     *   "successfulChannels": 2,
     *   "failedChannels": 1,
     *   "channels": [
     *     {
     *       "channelType": "EMAIL",
     *       "status": "DELIVERED",
     *       "recipientAddress": "user@example.com",
     *       "deliveredAt": "2025-01-15T10:30:00"
     *     },
     *     {
     *       "channelType": "SMS",
     *       "status": "FAILED",
     *       "recipientAddress": "+1234567890",
     *       "errorReason": "Invalid phone number",
     *       "errorCode": "INVALID_RECIPIENT",
     *       "retryCount": 3,
     *       "lastAttemptAt": "2025-01-15T10:35:00"
     *     },
     *     {
     *       "channelType": "POPUP",
     *       "status": "DELIVERED",
     *       "deliveredAt": "2025-01-15T10:30:00"
     *     }
     *   ],
     *   "message": "1 of 3 channels failed"
     * }
     *
     * @param appointmentId The appointment ID to query
     * @return NotificationStatusResponse with per-channel status
     */
    @GetMapping("/status")
    public ResponseEntity<NotificationStatusResponse> getNotificationStatus(
            @RequestParam String appointmentId) {

        log.info("Status query for appointmentId={}", appointmentId);

        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            NotificationStatusResponse response = notificationRetryService.getNotificationStatus(appointmentId);
            return ResponseEntity.ok(response);
        } catch (RecordNotFoundException e) {
            log.warn("No notifications found for appointmentId={}", appointmentId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error querying notification status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retry failed notifications for a specific appointment.
     * Can optionally retry only a specific channel type.
     *
     * POST /api/v1/notifications/retry
     *
     * Request Body Examples:
     * 1. Retry all failed channels:
     *    { "appointmentId": "APT123" }
     *
     * 2. Retry only SMS channel:
     *    { "appointmentId": "APT123", "channelType": "SMS" }
     *
     * Response Example:
     * {
     *   "appointmentId": "APT123",
     *   "channelType": "SMS",
     *   "eligibleForRetry": 1,
     *   "retriedCount": 1,
     *   "retriedChannels": ["SMS"],
     *   "success": true,
     *   "message": "Found 1 failed notifications, successfully submitted 1 for retry"
     * }
     *
     * @param retryRequest Contains appointment ID and optional channel type
     * @return Response with retry results
     */
    @PostMapping("/retry")
    public ResponseEntity<NotificationRetryResponse> retryNotifications(
            @RequestBody NotificationRetryRequest retryRequest) {

        log.info("Retry API called for appointmentId={}, channelType={}",
                retryRequest.getAppointmentId(), retryRequest.getChannelType());

        // Validate required fields
        if (retryRequest.getAppointmentId() == null || retryRequest.getAppointmentId().trim().isEmpty()) {
            NotificationRetryResponse errorResponse = NotificationRetryResponse.builder()
                    .appointmentId(retryRequest.getAppointmentId())
                    .eligibleForRetry(0)
                    .retriedCount(0)
                    .success(false)
                    .message("Appointment ID is required")
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            NotificationRetryResponse response = notificationRetryService.retryNotifications(retryRequest);
            return ResponseEntity.ok(response);
        } catch (RecordNotFoundException e) {
            log.warn("No notifications found for appointmentId={}", retryRequest.getAppointmentId());
            NotificationRetryResponse errorResponse = NotificationRetryResponse.builder()
                    .appointmentId(retryRequest.getAppointmentId())
                    .channelType(retryRequest.getChannelType())
                    .eligibleForRetry(0)
                    .retriedCount(0)
                    .success(false)
                    .message("No notifications found for appointmentId: " + retryRequest.getAppointmentId())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Error processing retry request: {}", e.getMessage(), e);
            NotificationRetryResponse errorResponse = NotificationRetryResponse.builder()
                    .appointmentId(retryRequest.getAppointmentId())
                    .channelType(retryRequest.getChannelType())
                    .eligibleForRetry(0)
                    .retriedCount(0)
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
