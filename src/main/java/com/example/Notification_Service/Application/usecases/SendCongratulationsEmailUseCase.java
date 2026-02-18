package com.example.Notification_Service.Application.usecases;

import com.example.Notification_Service.Domain.ApplicationEmailData;

/**
 * Use case interface for sending congratulation emails to applicants
 * when they submit a job application.
 */
public interface SendCongratulationsEmailUseCase {
    
    /**
     * Send a congratulations email to the applicant
     * @param data The application email data containing applicant and job information
     * @param userId The user ID for tracking/logging purposes
     */
    void sendCongratulationsEmail(ApplicationEmailData data, String userId);
}
