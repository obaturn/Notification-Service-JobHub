package com.example.Notification_Service.Domain;

/**
 * Data class containing all information needed to compose a congratulations email
 * when a user submits a job application.
 */
public record ApplicationEmailData(
    String applicantName,
    String applicantEmail,
    String jobTitle,
    String companyName,
    String appliedDate,
    String applicationId,
    String appName,
    String appLogoUrl,
    String appUrl,
    String unsubscribeUrl,
    String privacyUrl
) {
    /**
     * Generate the application status link
     */
    public String getApplicationStatusLink() {
        return appUrl + "/my-applications/" + applicationId;
    }
}
