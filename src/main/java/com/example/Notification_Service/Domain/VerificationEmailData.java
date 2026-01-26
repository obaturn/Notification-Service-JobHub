package com.example.Notification_Service.Domain;

public class VerificationEmailData {
    private String firstName;
    private String email;
    private String verificationToken;
    private String appUrl;
    private String appName;
    private String appLogoUrl;
    private String supportUrl;
    private int tokenExpirationHours;

    public VerificationEmailData() {}

    public VerificationEmailData(String firstName, String email, String verificationToken,
                                String appUrl, String appName, String appLogoUrl,
                                String supportUrl, int tokenExpirationHours) {
        this.firstName = firstName;
        this.email = email;
        this.verificationToken = verificationToken;
        this.appUrl = appUrl;
        this.appName = appName;
        this.appLogoUrl = appLogoUrl;
        this.supportUrl = supportUrl;
        this.tokenExpirationHours = tokenExpirationHours;
    }

    public String getVerificationLink() {
        return appUrl + "/verify-email?token=" + verificationToken;
    }

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public String getAppUrl() { return appUrl; }
    public void setAppUrl(String appUrl) { this.appUrl = appUrl; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getAppLogoUrl() { return appLogoUrl; }
    public void setAppLogoUrl(String appLogoUrl) { this.appLogoUrl = appLogoUrl; }

    public String getSupportUrl() { return supportUrl; }
    public void setSupportUrl(String supportUrl) { this.supportUrl = supportUrl; }

    public int getTokenExpirationHours() { return tokenExpirationHours; }
    public void setTokenExpirationHours(int tokenExpirationHours) { this.tokenExpirationHours = tokenExpirationHours; }
}