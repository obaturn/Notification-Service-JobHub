package com.example.Notification_Service.Domain;

public interface EmailNotificationService {
    EmailMessage composeVerificationEmail(VerificationEmailData data, String userId);
    
    EmailMessage composeCongratulationsEmail(ApplicationEmailData data, String userId);
}