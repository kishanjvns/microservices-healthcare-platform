package com.tech.kj.model;

import com.tech.kj.constants.ChannelType;
import com.tech.kj.constants.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryResult {
    private String renderedContent;
    private ChannelType channelType;
    private DeliveryStatus deliveryStatus;
    private int retry;
    private int maxRetries;
    private String errorMessage;
    private String errorCode;
    private String vendorResponse;
    private String vendorStatus;
}
