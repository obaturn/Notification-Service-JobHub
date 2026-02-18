package com.example.Notification_Service.Domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the Kafka event data when a job application is submitted.
 * This event is published by the Application Service to the application-events topic.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApplicationEventData(
    String eventType,
    String applicationId,
    Long jobId,
    String jobTitle,
    String companyName,
    String companyId,
    String employerId,
    String userId,
    String applicantName,
    String applicantEmail,
    String resumeId,
    String status,
    String appliedDate,
    String timestamp
) {
    /**
     * Check if this is an application submitted event
     */
    public boolean isApplicationSubmitted() {
        return "APPLICATION_SUBMITTED".equals(eventType);
    }
}
