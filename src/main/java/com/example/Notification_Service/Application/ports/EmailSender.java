package com.example.Notification_Service.Application.ports;

import com.example.Notification_Service.Domain.EmailMessage;

public interface EmailSender {
    void sendEmail(EmailMessage emailMessage);
}