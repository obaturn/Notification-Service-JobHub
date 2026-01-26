package com.example.Notification_Service.Domain;

public interface EmailNotificationService {
    EmailMessage composeVerificationEmail(VerificationEmailData data, String userId);
}