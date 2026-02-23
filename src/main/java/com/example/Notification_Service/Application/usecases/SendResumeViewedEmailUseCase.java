package com.example.Notification_Service.Application.usecases;

import com.example.Notification_Service.Domain.ResumeViewedEmailData;

/**
 * Use case interface for sending resume viewed emails to applicants.
 * This is triggered when an employer views/downloads an applicant's resume.
 */
public interface SendResumeViewedEmailUseCase {
    
    /**
     * Send a resume viewed notification email to the applicant.
     * 
     * @param data   The resume viewed email data containing applicant and job details
     * @param userId The user ID for tracking/logging purposes
     */
    void sendResumeViewedEmail(ResumeViewedEmailData data, String userId);
}
