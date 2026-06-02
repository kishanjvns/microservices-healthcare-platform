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
public class SMSNotificationSender extends NotificationChannelStrategy {

    @Autowired
    @Qualifier("SMSTemplateServiceImpl")
    TemplateService templateService;

    @Override
    public DeliveryResult send(NotificationRequestModel notificationRequestModel) throws RuntimeException {

        String content = templateService.render(notificationRequestModel);
        // TODO integrate to THIRD PARTY SMS PROVIDER TO SEND SMS
        log.info("sms send content: {}",content);
        // TODO below vendorStatus,vendorResponse,errorMessage,errorCode will be according to the response of THIRD PARTY SMS PROVIDER

        return DeliveryResult.builder()
                .renderedContent(content)
                .channelType(ChannelType.SMS)
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
