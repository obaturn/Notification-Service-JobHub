package com.example.Notification_Service.Presentation;

import com.example.Notification_Service.Application.usecases.SendVerificationEmailUseCase;
import com.example.Notification_Service.Domain.UserRegisteredEvent;
import com.example.Notification_Service.Domain.VerificationEmailData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventListener.class);

    private final SendVerificationEmailUseCase sendVerificationEmailUseCase;
    private final ObjectMapper objectMapper;

    @Value("${app.name:JobHub}")
    private String appName;

    @Value("${app.logo-url:https://jobhub.com/logo.png}")
    private String appLogoUrl;

    @Value("${app.url:http://localhost:3000}")
    private String appUrl;

    @Value("${app.support-url:http://localhost:3000/support}")
    private String supportUrl;

    @Value("${app.token-expiry-hours:24}")
    private int tokenExpiryHours;

    public KafkaEventListener(SendVerificationEmailUseCase sendVerificationEmailUseCase, ObjectMapper objectMapper) {
        this.sendVerificationEmailUseCase = sendVerificationEmailUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void onUserRegistered(String message) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);
            logger.info("Received UserRegistered event for userId: {}", event.userId());
            if ("UserRegistered".equals(event.eventType())) {
                VerificationEmailData emailData = new VerificationEmailData(
                    event.firstName(),
                    event.email(),
                    event.verificationToken(),
                    appUrl,
                    appName,
                    appLogoUrl,
                    supportUrl,
                    tokenExpiryHours
                );

                sendVerificationEmailUseCase.sendVerificationEmail(emailData, event.userId());
                logger.info("Verification email sent for userId: {}", event.userId());
            } else {
                logger.warn("Unknown event type: {}", event.eventType());
            }
        } catch (Exception e) {
            logger.error("Error processing Kafka message: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing Kafka message", e);
        }
    }
}