package com.tech.kj.service;

import com.tech.kj.constants.ChannelType;
import com.tech.kj.constants.NotificationType;
import com.tech.kj.entity.NotificationCategory;
import com.tech.kj.entity.NotificationTemplate;
import com.tech.kj.exceptions.RecordNotFoundException;
import com.tech.kj.model.NotificationRequestModel;
import com.tech.kj.repository.NotificationCategoryRepo;
import com.tech.kj.repository.NotificationTemplateRepo;

public abstract class TemplateService {

    protected final NotificationTemplateRepo templateRepository;
    protected final NotificationCategoryRepo categoryRepository;

    public TemplateService(NotificationTemplateRepo notificationTemplateRepo,NotificationCategoryRepo  categoryRepository) {
        this.templateRepository = notificationTemplateRepo;
        this.categoryRepository = categoryRepository;
    }

    protected NotificationTemplate getTemplate(NotificationType type, ChannelType channel) {
        NotificationCategory category = categoryRepository
                .findByNotificationType(type.name())
                .orElseThrow(() -> new RecordNotFoundException(String.format("NotificationCategory not found by %d type",type)));

        return templateRepository
                .findByNotificationCategoryIdAndChannelType(category.getId(), channel)
                .orElseThrow(() -> new RecordNotFoundException(String.format("NotificationTemplate not found with %s type and %s and channel ",type, channel)));
    }
    protected String renderContent(String templateContent, NotificationRequestModel model) {
        var content = templateContent;
        content = content.replace("${recipientName}", safe(model.getRecipientName()));
        content = content.replace("${doctorName}", safe(model.getDoctorName()));
        content = content.replace("${appointmentDate}", safe(model.getAppointmentDate()));
        content = content.replace("${appointmentTime}", safe(model.getAppointmentTime()));
        content = content.replace("${appointmentId}", safe(model.getAppointmentId()));

        return content;
    }
    private String safe(String value) {
        return value == null ? "" : value;
    }

    public abstract String render(NotificationRequestModel notificationRequestModel);
}
