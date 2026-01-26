package com.example.Notification_Service.Application.usecases;

import com.example.Notification_Service.Application.ports.EmailSender;
import com.example.Notification_Service.Domain.EmailMessage;
import com.example.Notification_Service.Domain.EmailNotificationService;
import com.example.Notification_Service.Domain.VerificationEmailData;
import org.springframework.stereotype.Service;

@Service
public class SendVerificationEmailUseCaseImpl implements SendVerificationEmailUseCase {

    private final EmailNotificationService emailNotificationService;
    private final EmailSender emailSender;

    public SendVerificationEmailUseCaseImpl(EmailNotificationService emailNotificationService, EmailSender emailSender) {
        this.emailNotificationService = emailNotificationService;
        this.emailSender = emailSender;
    }

    @Override
    public void sendVerificationEmail(VerificationEmailData data, String userId) {
        EmailMessage emailMessage = emailNotificationService.composeVerificationEmail(data, userId);
        emailSender.sendEmail(emailMessage);
    }
}