package com.tech.kj.service;

import com.tech.kj.constants.ChannelType;
import com.tech.kj.entity.NotificationTemplate;
import com.tech.kj.model.NotificationRequestModel;
import com.tech.kj.repository.NotificationCategoryRepo;
import com.tech.kj.repository.NotificationTemplateRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;


@Service
@Slf4j
public class SMSTemplateSeviceImpl extends TemplateService {

    public SMSTemplateSeviceImpl(@Autowired NotificationTemplateRepo notificationTemplateRepo, @Autowired NotificationCategoryRepo categoryRepository) {
        super(notificationTemplateRepo, categoryRepository);
    }

    @Override
    public String render(NotificationRequestModel model) {
        NotificationTemplate notificationTemplate = getTemplate(model.getNotificationType(), ChannelType.SMS);
        return renderContent(notificationTemplate.getTemplateContent(), model);
    }


}
