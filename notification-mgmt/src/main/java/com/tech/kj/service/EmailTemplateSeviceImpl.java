package com.tech.kj.service;

import com.tech.kj.constants.ChannelType;
import com.tech.kj.entity.NotificationTemplate;
import com.tech.kj.model.NotificationRequestModel;
import com.tech.kj.repository.NotificationCategoryRepo;
import com.tech.kj.repository.NotificationTemplateRepo;
import org.springframework.stereotype.Service;

@Service("emailTemplateServiceImpl")
public class EmailTemplateSeviceImpl extends TemplateService{
    public EmailTemplateSeviceImpl(NotificationTemplateRepo notificationTemplateRepo, NotificationCategoryRepo categoryRepository) {
        super(notificationTemplateRepo, categoryRepository);
    }

    @Override
    public String render(NotificationRequestModel model) {

        NotificationTemplate notificationTemplate = getTemplate(model.getNotificationType(), ChannelType.EMAIL);
        var result =  renderContent(notificationTemplate.getTemplateContent(), model);
        result = result.replace("${clinicName}",model.getClinicName());
        result = result.replace("${clinicAddress}",model.getClinicAddress());

        // TODO fetch from DB
        result = result.replace("${rescheduleUrl}","");
        result = result.replace("${cancelUrl}","");
        return result;
    }
}
