package com.tech.kj.service;

import com.tech.kj.constants.ChannelType;
import com.tech.kj.constants.DeliveryStatus;
import com.tech.kj.model.DeliveryResult;
import com.tech.kj.model.NotificationRequestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationSender extends NotificationChannelStrategy {
    @Autowired
    @Qualifier("emailTemplateServiceImpl")
    private TemplateService templateService;

    @Override
    public DeliveryResult send(NotificationRequestModel notificationRequestModel) throws RuntimeException {
        String content = templateService.render(notificationRequestModel);
        // TODO integrate to THIRD PARTY EMAIL PROVIDER TO SEND EMAIL
        log.info("email send content: {}",content);
        // TODO below vendorStatus,vendorResponse,errorMessage,errorCode will be according to the response of THIRD PARTY EMAIL PROVIDER

        return DeliveryResult.builder()
                .renderedContent(content)
                .channelType(ChannelType.EMAIL)
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .retry(1)
                .maxRetries(1)
                .errorMessage("")
                .errorCode("")
                .vendorResponse("Success")
                .vendorStatus("Success")
                .build();
    }

}
