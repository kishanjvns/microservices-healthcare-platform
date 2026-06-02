package com.tech.kj.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tech.kj.constants.ChannelType;
import com.tech.kj.constants.DeliveryStatus;
import com.tech.kj.entity.NotificationEntity;
import com.tech.kj.model.DeliveryResult;
import com.tech.kj.model.NotificationRequestModel;
import com.tech.kj.repository.NotificationRepo;
import com.tech.kj.service.NotificationChannelStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("sendPopupCommand")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SendPopupCommand extends NotificationCommand {

    @Autowired
    private NotificationChannelStrategy notificationChannelStrategy;

    @Autowired
    private NotificationRepo notificationRepo;

    @Value("${app.notification.popup.max-retries}")
    protected int maxRetries;

    @Value("${app.notification.popup.retry-delay-ms}")
    protected int retryDelayMs;

    /**
     * Implements idempotency check for popup notifications.
     * Checks NotificationEntity table for existing outboxEventId.
     */
    @Override
    protected boolean existsByOutboxEventId(String outboxEventId) {
        return notificationRepo.existsByOutboxEventId(outboxEventId);
    }

    @Override
    @Transactional
    public void execute() throws JsonProcessingException {
        NotificationRequestModel notificationRequestModel = objectMapper.readValue(getData(), NotificationRequestModel.class);

        // ✅ Command-level idempotency check
        if (isAlreadyProcessed(notificationRequestModel.getOutboxEventId())) {
            log.info("Popup notification already processed for outboxEventId={}, skipping",
                    notificationRequestModel.getOutboxEventId());
            return;
        }

        DeliveryResult finalResult = null;
        int attemptCount = 0;

        while (attemptCount <= maxRetries) {
            try {
                log.info("Sending Popup notification, attempt {} of {}", attemptCount + 1, maxRetries + 1);
                finalResult = notificationChannelStrategy.send(notificationRequestModel);

                if (DeliveryStatus.DELIVERED.equals(finalResult.getDeliveryStatus())) {
                    log.info("Popup notification delivered successfully on attempt {}", attemptCount + 1);
                    break;
                }
                log.warn("Popup notification failed on attempt {}. Status: {}, Error: {}",
                        attemptCount + 1,
                        finalResult.getDeliveryStatus(),
                        finalResult.getErrorMessage());
            } catch (Exception e) {
                log.error("Exception during Popup send on attempt {}: {}", attemptCount + 1, e.getMessage(), e);

                finalResult = DeliveryResult.builder()
                        .renderedContent("")
                        .channelType(ChannelType.POPUP)
                        .deliveryStatus(DeliveryStatus.FAILED)
                        .errorMessage(e.getMessage())
                        .errorCode("SEND_EXCEPTION")
                        .vendorResponse("")
                        .vendorStatus("EXCEPTION")
                        .build();
            }

            if (attemptCount < maxRetries && !DeliveryStatus.DELIVERED.equals(finalResult.getDeliveryStatus())) {
                long delay = retryDelayMs * (long) Math.pow(2, attemptCount);
                log.info("Retrying Popup notification in {}ms...", delay);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("Popup retry interrupted, stopping retry attempts");
                    break;
                }
            }

            attemptCount++;
        }

        var notificationEntity = createNotificationWithDelivery(
                attemptCount,
                maxRetries,
                finalResult,
                notificationRequestModel
        );

        notificationRepo.save(notificationEntity);

        log.info("Popup notification saved with ID: {} after {} attempt(s). Final status: {}",
                notificationEntity.getId(),
                attemptCount + 1,
                finalResult.getDeliveryStatus());
    }
}
