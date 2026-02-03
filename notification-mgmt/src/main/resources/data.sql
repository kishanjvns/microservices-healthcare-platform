INSERT INTO notification_category (id, category_name, description) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'APPOINTMENT_CREATED', 'When a new appointment is booked');


-- SMS Template (160 char limit awareness)
INSERT INTO notification_template
(id, notification_category_id, channel_type, template_content, subject)
VALUES (
    '550e8400-e29b-41d4-a716-446655441112',
    '550e8400-e29b-41d4-a716-446655440001',
    'SMS',
    'Hi ${recipientName}, your appointment with Dr. ${doctorName} is confirmed for ${appointmentDate} at ${appointmentTime}. Ref: ${appointmentId}. Reply HELP for assistance.',
    NULL
);

-- Email Template (HTML)
INSERT INTO notification_template
(id, notification_category_id, channel_type, template_content, subject)
VALUES (
    '550e8400-e29b-41d4-a716-446655441113',
    '550e8400-e29b-41d4-a716-446655440001',
    'EMAIL',
    '<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #2c5aa0; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background-color: #f9f9f9; }
        .appointment-card { background: white; border-radius: 8px; padding: 20px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .detail-row { display: flex; padding: 10px 0; border-bottom: 1px solid #eee; }
        .detail-label { font-weight: bold; width: 140px; color: #666; }
        .detail-value { flex: 1; }
        .button { display: inline-block; background-color: #2c5aa0; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; margin: 5px; }
        .button-secondary { background-color: #6c757d; }
        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Appointment Confirmed</h1>
        </div>
        <div class="content">
            <p>Dear ${recipientName},</p>
            <p>Your appointment has been successfully scheduled. Please find the details below:</p>
            
            <div class="appointment-card">
                <div class="detail-row">
                    <span class="detail-label">Appointment ID:</span>
                    <span class="detail-value">${appointmentId}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Doctor:</span>
                    <span class="detail-value">Dr. ${doctorName}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Date:</span>
                    <span class="detail-value">${appointmentDate}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Time:</span>
                    <span class="detail-value">${appointmentTime}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Location:</span>
                    <span class="detail-value">${clinicName}<br>${clinicAddress}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Contact:</span>
                    <span class="detail-value">${clinicPhone}</span>
                </div>
            </div>
            
            <p style="text-align: center;">
                <a href="${rescheduleUrl}" class="button">Reschedule</a>
                <a href="${cancelUrl}" class="button button-secondary">Cancel</a>
            </p>
            
            <p><strong>Important Reminders:</strong></p>
            <ul>
                <li>Please arrive 15 minutes before your scheduled time</li>
                <li>Bring your insurance card and photo ID</li>
                <li>Bring a list of current medications</li>
            </ul>
        </div>
        <div class="footer">
            <p>${clinicName} | ${clinicAddress}</p>
            <p>Questions? Call us at ${clinicPhone}</p>
        </div>
    </div>
</body>
</html>',
    'Appointment Confirmed - Dr. ${doctorName} on ${appointmentDate}'
);

-- Popup Template (JSON)
INSERT INTO notification_template
(id, notification_category_id, channel_type, template_content, subject)
VALUES (
    '550e8400-e29b-41d4-a716-446655441114',
    '550e8400-e29b-41d4-a716-446655440001',
    'POPUP',
    '{
        "title": "Appointment Confirmed ✓",
        "body": "Your appointment with Dr. ${doctorName} is scheduled for ${appointmentDate} at ${appointmentTime}.",
        "icon": "calendar-check",
        "type": "success",
        "actions": [
            {"label": "View Details", "action": "VIEW_APPOINTMENT", "data": "${appointmentId}"},
            {"label": "Add to Calendar", "action": "ADD_CALENDAR", "data": "${appointmentDateTime}"}
        ],
        "metadata": {
            "appointmentId": "${appointmentId}",
            "priority": "normal"
        }
    }',
    NULL
);