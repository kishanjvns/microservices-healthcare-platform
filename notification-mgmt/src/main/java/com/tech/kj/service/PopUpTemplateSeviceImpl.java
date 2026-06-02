package com.tech.kj.service;

import com.tech.kj.constants.ChannelType;
import com.tech.kj.entity.NotificationTemplate;
import com.tech.kj.model.NotificationRequestModel;
import com.tech.kj.repository.NotificationCategoryRepo;
import com.tech.kj.repository.NotificationTemplateRepo;
import org.springframework.stereotype.Service;

@Service("popUpTemplateSeviceImpl")
public class PopUpTemplateSeviceImpl extends TemplateService{
    public PopUpTemplateSeviceImpl(NotificationTemplateRepo notificationTemplateRepo, NotificationCategoryRepo categoryRepository) {
        super(notificationTemplateRepo, categoryRepository);
    }

    @Override
    public String render(NotificationRequestModel model) {

        NotificationTemplate notificationTemplate = getTemplate(model.getNotificationType(), ChannelType.POPUP);
        var result =  renderContent(notificationTemplate.getTemplateContent(), model);
        result = result.replace("${appointmentDateTime}",model.getAppointmentDate()+":"+model.getAppointmentTime());
        return result;
    }
}
