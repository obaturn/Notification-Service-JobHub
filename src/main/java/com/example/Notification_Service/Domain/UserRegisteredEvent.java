package com.example.Notification_Service.Domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRegisteredEvent(String eventType, String userId, String email, String verificationToken, String firstName, Instant timestamp) {
}
