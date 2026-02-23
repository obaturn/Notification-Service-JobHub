package com.example.Notification_Service.Application.usecases;

import com.example.Notification_Service.Application.ports.EmailSender;
import com.example.Notification_Service.Domain.EmailMessage;
import com.example.Notification_Service.Domain.EmailNotificationService;
import com.example.Notification_Service.Domain.ResumeViewedEmailData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the SendResumeViewedEmailUseCase.
 * Handles the business logic for sending resume viewed notification emails to applicants.
 * Similar to LinkedIn's "Your profile was viewed" notification.
 */
@Service
public class SendResumeViewedEmailUseCaseImpl implements SendResumeViewedEmailUseCase {

    private static final Logger logger = LoggerFactory.getLogger(SendResumeViewedEmailUseCaseImpl.class);

    private final EmailNotificationService emailNotificationService;
    private final EmailSender emailSender;

    public SendResumeViewedEmailUseCaseImpl(
            EmailNotificationService emailNotificationService,
            EmailSender emailSender) {
        this.emailNotificationService = emailNotificationService;
        this.emailSender = emailSender;
    }

    @Override
    public void sendResumeViewedEmail(ResumeViewedEmailData data, String userId) {
        logger.info("📧 Sending resume viewed notification email to applicant: {}", data.applicantEmail());
        
        try {
            // Compose the resume viewed notification email
            EmailMessage emailMessage = emailNotificationService.composeResumeViewedEmail(data, userId);
            
            // Send the email
            emailSender.sendEmail(emailMessage);
            
            logger.info("✅ Resume viewed notification email sent successfully to: {}", data.applicantEmail());
            
        } catch (Exception e) {
            logger.error("❌ Failed to send resume viewed notification email to: {}", data.applicantEmail(), e);
            throw new RuntimeException("Failed to send resume viewed notification email", e);
        }
    }
}
