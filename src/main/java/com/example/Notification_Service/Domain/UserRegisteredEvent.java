package com.example.Notification_Service.Domain;

public record UserRegisteredEvent(String eventType, String userId, String email, String verificationToken, String firstName) {
}