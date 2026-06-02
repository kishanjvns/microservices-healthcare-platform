package com.tech.kj.service;

import com.tech.kj.model.DeliveryResult;
import com.tech.kj.model.NotificationRequestModel;

public abstract class NotificationChannelStrategy {

    public abstract DeliveryResult send(NotificationRequestModel notificationRequestModel) throws RuntimeException;
}
