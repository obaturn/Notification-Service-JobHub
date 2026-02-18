package com.example.Notification_Service.Application.usecases;

import com.example.Notification_Service.Application.ports.EmailSender;
import com.example.Notification_Service.Domain.ApplicationEmailData;
import com.example.Notification_Service.Domain.EmailMessage;
import com.example.Notification_Service.Domain.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the SendCongratulationsEmailUseCase.
 * Handles the business logic for sending congratulation emails to applicants.
 */
@Service
public class SendCongratulationsEmailUseCaseImpl implements SendCongratulationsEmailUseCase {

    private static final Logger logger = LoggerFactory.getLogger(SendCongratulationsEmailUseCaseImpl.class);

    private final EmailNotificationService emailNotificationService;
    private final EmailSender emailSender;

    public SendCongratulationsEmailUseCaseImpl(
            EmailNotificationService emailNotificationService,
            EmailSender emailSender) {
        this.emailNotificationService = emailNotificationService;
        this.emailSender = emailSender;
    }

    @Override
    public void sendCongratulationsEmail(ApplicationEmailData data, String userId) {
        logger.info("📧 Sending congratulations email to applicant: {}", data.applicantEmail());
        
        try {
            // Compose the congratulations email
            EmailMessage emailMessage = emailNotificationService.composeCongratulationsEmail(data, userId);
            
            // Send the email
            emailSender.sendEmail(emailMessage);
            
            logger.info("✅ Congratulations email sent successfully to: {}", data.applicantEmail());
            
        } catch (Exception e) {
            logger.error("❌ Failed to send congratulations email to: {}", data.applicantEmail(), e);
            throw new RuntimeException("Failed to send congratulations email", e);
        }
    }
}
