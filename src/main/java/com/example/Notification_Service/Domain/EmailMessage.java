package com.example.Notification_Service.Domain;

public record EmailMessage(String to, String subject, String body, String userId) {
}