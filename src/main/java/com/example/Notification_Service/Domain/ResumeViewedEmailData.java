package com.example.Notification_Service.Domain;

/**
 * Data class containing all information needed to compose a resume viewed email
 * when an employer views an applicant's resume.
 * This is similar to LinkedIn's "Your profile was viewed" notification.
 */
public record ResumeViewedEmailData(
    String applicantName,
    String applicantEmail,
    String jobTitle,
    String companyName,
    String companyId,
    String employerId,
    String userId,
    String applicationId,
    String viewedDate,
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

    /**
     * Generate the job details link
     */
    public String getJobDetailsLink() {
        return appUrl + "/jobs/" + applicationId;
    }

    /**
     * Get company profile link
     */
    public String getCompanyProfileLink() {
        return appUrl + "/companies/" + companyId;
    }
}
