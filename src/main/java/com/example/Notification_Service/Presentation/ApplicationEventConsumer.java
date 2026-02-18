package com.example.Notification_Service.Presentation;

import com.example.Notification_Service.Application.usecases.SendCongratulationsEmailUseCase;
import com.example.Notification_Service.Domain.ApplicationEmailData;
import com.example.Notification_Service.Domain.ApplicationEventData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that listens to the application-events topic and sends
 * congratulation emails to applicants when they submit a job application.
 */
@Component
public class ApplicationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationEventConsumer.class);

    private final SendCongratulationsEmailUseCase sendCongratulationsEmailUseCase;
    private final ObjectMapper objectMapper;

    @Value("${app.name:JobHub}")
    private String appName;

    @Value("${app.logo-url:https://jobhub.com/logo.png}")
    private String appLogoUrl;

    @Value("${app.url:http://localhost:3000}")
    private String appUrl;

    @Value("${app.unsubscribe-url:http://localhost:3000/unsubscribe}")
    private String unsubscribeUrl;

    @Value("${app.privacy-url:http://localhost:3000/privacy}")
    private String privacyUrl;

    public ApplicationEventConsumer(
            SendCongratulationsEmailUseCase sendCongratulationsEmailUseCase,
            ObjectMapper objectMapper) {
        this.sendCongratulationsEmailUseCase = sendCongratulationsEmailUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = "application-events", 
        groupId = "notification-service",
        containerFactory = "manualAckContainerFactory"
    )
    public void onApplicationEvent(String message, Acknowledgment ack) {
        try {
            ApplicationEventData event = objectMapper.readValue(message, ApplicationEventData.class);
            logger.info("📥 Received application event: eventType={}, applicationId={}, applicantEmail={}", 
                event.eventType(), event.applicationId(), event.applicantEmail());
            
            if (event.isApplicationSubmitted()) {
                // Process the event and send congratulations email
                processApplicationSubmittedEvent(event);
                
                // Acknowledge successful processing
                ack.acknowledge();
                logger.debug("✅ Message acknowledged for applicationId: {}", event.applicationId());
            } else {
                logger.warn("⚠️ Unknown event type: {} for applicationId: {}", 
                    event.eventType(), event.applicationId());
                // Acknowledge unknown events to avoid reprocessing
                ack.acknowledge();
            }
            
        } catch (Exception e) {
            logger.error("❌ Error processing Kafka message: {}", e.getMessage(), e);
            // Don't acknowledge - let the error handler retry or send to DLT
            throw new RuntimeException("Error processing application event", e);
        }
    }

    /**
     * Process the APPLICATION_SUBMITTED event and send congratulations email
     */
    private void processApplicationSubmittedEvent(ApplicationEventData event) {
        logger.info("📧 Processing application submitted event for: {} - {} at {}", 
            event.applicantName(), event.jobTitle(), event.companyName());
        
        // Prepare email data
        ApplicationEmailData emailData = new ApplicationEmailData(
            event.applicantName(),
            event.applicantEmail(),
            event.jobTitle(),
            event.companyName(),
            event.appliedDate(),
            event.applicationId(),
            appName,
            appLogoUrl,
            appUrl,
            unsubscribeUrl,
            privacyUrl
        );
        
        // Send congratulations email
        sendCongratulationsEmailUseCase.sendCongratulationsEmail(emailData, event.userId());
        
        logger.info("✅ Congratulations email processing completed for applicationId: {}", 
            event.applicationId());
    }
}
