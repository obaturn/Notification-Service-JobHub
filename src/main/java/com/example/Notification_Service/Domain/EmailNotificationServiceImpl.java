package com.example.Notification_Service.Domain;

import org.springframework.stereotype.Service;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

    @Override
    public EmailMessage composeVerificationEmail(VerificationEmailData data, String userId) {
        String subject = "Verify Your Email Address - Welcome to " + data.getAppName() + "!";

        String htmlBody = String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verify Your Email - %s</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
                        font-size: 14px;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f5f5f5;
                        padding: 20px;
                    }

                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                    }

                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        padding: 40px 30px;
                        text-align: center;
                        color: white;
                    }

                    .logo {
                        display: inline-block;
                        margin-bottom: 20px;
                    }

                    .logo img {
                        height: 50px;
                        width: auto;
                    }

                    .header h1 {
                        font-size: 28px;
                        font-weight: 600;
                        margin-bottom: 10px;
                        letter-spacing: -0.5px;
                    }

                    .header p {
                        font-size: 16px;
                        opacity: 0.95;
                    }

                    .content {
                        padding: 40px 30px;
                    }

                    .greeting {
                        font-size: 18px;
                        font-weight: 500;
                        color: #1a1a1a;
                        margin-bottom: 20px;
                    }

                    .greeting strong {
                        color: #667eea;
                    }

                    .description {
                        color: #555;
                        margin-bottom: 30px;
                        line-height: 1.8;
                    }

                    .cta-section {
                        text-align: center;
                        margin: 40px 0;
                        padding: 30px 0;
                    }

                    .cta-button {
                        display: inline-block;
                        padding: 14px 40px;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        text-decoration: none;
                        border-radius: 6px;
                        font-weight: 600;
                        font-size: 16px;
                        transition: all 0.3s ease;
                        border: 2px solid transparent;
                        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                    }

                    .cta-button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
                    }

                    .backup-link {
                        background-color: #f9fafb;
                        border-left: 4px solid #667eea;
                        padding: 20px;
                        margin: 30px 0;
                        border-radius: 4px;
                    }

                    .backup-link-label {
                        font-size: 12px;
                        color: #888;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                        margin-bottom: 10px;
                    }

                    .backup-link-text {
                        word-break: break-all;
                        font-family: 'Courier New', monospace;
                        font-size: 12px;
                        color: #667eea;
                        background-color: white;
                        padding: 10px;
                        border-radius: 3px;
                        border: 1px solid #e0e0e0;
                    }

                    .features {
                        margin: 40px 0;
                        padding: 30px;
                        background-color: #f0f4ff;
                        border-radius: 6px;
                    }

                    .features-title {
                        font-size: 14px;
                        font-weight: 600;
                        color: #667eea;
                        margin-bottom: 15px;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }

                    .features ul {
                        list-style: none;
                        padding: 0;
                    }

                    .features li {
                        padding: 8px 0;
                        color: #333;
                        font-size: 13px;
                        padding-left: 25px;
                        position: relative;
                    }

                    .features li:before {
                        content: "✓";
                        position: absolute;
                        left: 0;
                        color: #667eea;
                        font-weight: bold;
                        font-size: 14px;
                    }

                    .footer {
                        background-color: #f5f5f5;
                        padding: 30px;
                        text-align: center;
                        border-top: 1px solid #e0e0e0;
                    }

                    .footer-text {
                        font-size: 12px;
                        color: #888;
                        margin-bottom: 15px;
                        line-height: 1.6;
                    }

                    .footer-links {
                        margin: 15px 0;
                    }

                    .footer-links a {
                        color: #667eea;
                        text-decoration: none;
                        font-size: 12px;
                        margin: 0 10px;
                    }

                    .footer-links a:hover {
                        text-decoration: underline;
                    }

                    .warning {
                        background-color: #fff3cd;
                        border: 1px solid #ffc107;
                        border-radius: 4px;
                        padding: 15px;
                        margin: 20px 0;
                        color: #856404;
                        font-size: 13px;
                        text-align: center;
                    }

                    @media only screen and (max-width: 600px) {
                        .container {
                            margin: 0;
                            border-radius: 0;
                        }

                        .header {
                            padding: 30px 20px;
                        }

                        .header h1 {
                            font-size: 24px;
                        }

                        .content {
                            padding: 20px;
                        }

                        .cta-button {
                            display: block;
                            width: 100%%;
                        }

                        .footer {
                            padding: 20px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <!-- Header Section -->
                    <div class="header">
                        <div class="logo">
                            <img src="%s" alt="%s Logo" style="height: 45px;">
                        </div>
                        <h1>Welcome to %s! 🎉</h1>
                        <p>Let's get you started on your professional journey</p>
                    </div>

                    <!-- Main Content -->
                    <div class="content">
                        <!-- Greeting -->
                        <p class="greeting">Hi <strong>%s</strong>,</p>

                        <!-- Welcome Message -->
                        <div class="description">
                            <p>Thank you for joining %s! We're thrilled to have you as part of our professional community.</p>
                            <p style="margin-top: 15px;">To complete your registration and get full access to all features, please verify your email address by clicking the button below:</p>
                        </div>

                        <!-- Call-to-Action Button -->
                        <div class="cta-section">
                            <a href="%s" class="cta-button">
                                ✓ Verify Email Address
                            </a>
                        </div>

                        <!-- Backup Link -->
                        <div class="backup-link">
                            <div class="backup-link-label">Or copy this link:</div>
                            <div class="backup-link-text">%s</div>
                        </div>

                        <!-- Expiration Warning -->
                        <div class="warning">
                            ⏰ This verification link will expire in %d hours. Please verify your email soon.
                        </div>

                        <!-- Features Section -->
                        <div class="features">
                            <div class="features-title">Here's what you can do with %s:</div>
                            <ul>
                                <li>Search and apply for jobs matching your skills</li>
                                <li>Build your professional profile and showcase your expertise</li>
                                <li>Get personalized job recommendations</li>
                                <li>Network with professionals in your industry</li>
                                <li>Track your applications and interview progress</li>
                            </ul>
                        </div>
                    </div>

                    <!-- Footer Section -->
                    <div class="footer">
                        <p class="footer-text">
                            If you didn't create this account, please ignore this email or <a href="%s" style="color: #667eea;">contact our support team</a>.
                        </p>

                        <div class="footer-links">
                            <a href="%s/privacy">Privacy Policy</a>
                            <a href="%s/terms">Terms of Service</a>
                            <a href="%s">Support</a>
                        </div>

                        <div class="footer-divider"></div>

                        <p class="footer-text">
                            © 2026 %s. All rights reserved.<br>
                            <strong>%s Inc.</strong> | Professional Job Marketplace
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, data.getAppName(), data.getAppLogoUrl(), data.getAppName(), data.getAppName(), data.getFirstName(), data.getAppName(), data.getVerificationLink(), data.getVerificationLink(), data.getTokenExpirationHours(), data.getAppName(), data.getSupportUrl(), data.getAppUrl(), data.getAppUrl(), data.getSupportUrl(), data.getAppName(), data.getAppName());

        return new EmailMessage(data.getEmail(), subject, htmlBody, userId);
    }

    @Override
    public EmailMessage composeCongratulationsEmail(ApplicationEmailData data, String userId) {
        String subject = String.format("🎉 Application Submitted! %s at %s", data.jobTitle(), data.companyName());

        String htmlBody = String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Application Submitted - %s</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        font-size: 14px;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f4f4f4;
                        padding: 20px;
                    }

                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: #ffffff;
                        border-radius: 12px;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                    }

                    .header {
                        background: linear-gradient(135deg, #0061f2 0%%, #00c6f7 100%%);
                        padding: 30px 40px;
                        text-align: center;
                    }

                    .header img {
                        display: block;
                        margin: 0 auto;
                        width: 120px;
                    }

                    .header p {
                        color: rgba(255, 255, 255, 0.9);
                        font-size: 14px;
                        margin: 10px 0 0 0;
                    }

                    .content {
                        padding: 40px;
                    }

                    .congrats-header {
                        text-align: center;
                        padding-bottom: 20px;
                    }

                    .congrats-header .emoji {
                        font-size: 48px;
                    }

                    .congrats-header h1 {
                        color: #1a1a1a;
                        font-size: 24px;
                        margin: 15px 0 0 0;
                        font-weight: 600;
                    }

                    .congrats-header p {
                        color: #666666;
                        font-size: 16px;
                        margin: 10px 0 0 0;
                    }

                    .details-card {
                        background-color: #f8f9fa;
                        border-radius: 12px;
                        margin-top: 25px;
                        padding: 25px;
                    }

                    .details-card h2 {
                        color: #0061f2;
                        font-size: 18px;
                        margin: 0 0 20px 0;
                        font-weight: 600;
                    }

                    .detail-row {
                        padding: 10px 0;
                        border-bottom: 1px solid #e9ecef;
                    }

                    .detail-row:last-child {
                        border-bottom: none;
                    }

                    .detail-label {
                        color: #666666;
                        font-size: 14px;
                    }

                    .detail-value {
                        color: #1a1a1a;
                        font-size: 16px;
                        font-weight: 500;
                        margin-top: 4px;
                    }

                    .next-steps {
                        background-color: #e8f5e9;
                        border-left: 4px solid #4caf50;
                        padding: 15px 20px;
                        border-radius: 0 8px 8px 0;
                        margin-top: 30px;
                    }

                    .next-steps p {
                        color: #2e7d32;
                        font-size: 14px;
                        margin: 0;
                    }

                    .cta-section {
                        margin-top: 30px;
                        text-align: center;
                    }

                    .cta-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #0061f2 0%%, #00c6f7 100%%);
                        color: #ffffff;
                        text-decoration: none;
                        padding: 14px 32px;
                        border-radius: 8px;
                        font-size: 16px;
                        font-weight: 600;
                    }

                    .footer {
                        margin-top: 40px;
                        padding-top: 20px;
                        border-top: 1px solid #e9ecef;
                        text-align: center;
                    }

                    .footer p {
                        color: #999999;
                        font-size: 12px;
                        margin: 0;
                    }

                    .footer p:not(:first-child) {
                        margin-top: 10px;
                    }

                    .footer a {
                        color: #0061f2;
                        text-decoration: none;
                    }

                    @media only screen and (max-width: 600px) {
                        .container {
                            margin: 0;
                            border-radius: 0;
                        }

                        .header {
                            padding: 20px;
                        }

                        .content {
                            padding: 20px;
                        }

                        .cta-button {
                            display: block;
                            width: 100%%;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <!-- Header with JobHub Logo -->
                    <div class="header">
                        <img src="%s" alt="%s Logo" width="120">
                        <p>Your Professional Job Marketplace</p>
                    </div>

                    <!-- Main Content -->
                    <div class="content">
                        <!-- Congratulation Header -->
                        <div class="congrats-header">
                            <span class="emoji">🎉</span>
                            <h1>Congratulations!</h1>
                            <p>Your application has been submitted successfully</p>
                        </div>

                        <!-- Application Details Card -->
                        <div class="details-card">
                            <h2>📋 Application Details</h2>

                            <div class="detail-row">
                                <div class="detail-label">Job Title</div>
                                <div class="detail-value">%s</div>
                            </div>

                            <div class="detail-row">
                                <div class="detail-label">Company</div>
                                <div class="detail-value">%s</div>
                            </div>

                            <div class="detail-row">
                                <div class="detail-label">Applied On</div>
                                <div class="detail-value">%s</div>
                            </div>
                        </div>

                        <!-- Next Steps -->
                        <div class="next-steps">
                            <p>✅ <strong>Next Steps:</strong> The company will review your application and get back to you soon. Keep an eye on your email and %s notifications!</p>
                        </div>

                        <!-- CTA Button -->
                        <div class="cta-section">
                            <a href="%s" class="cta-button">
                                View Application Status
                            </a>
                        </div>

                        <!-- Footer -->
                        <div class="footer">
                            <p>Good luck with your application! 🍀</p>
                            <p>© 2026 %s. All rights reserved.</p>
                            <p>
                                <a href="%s">Unsubscribe</a> |
                                <a href="%s">Privacy Policy</a>
                            </p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """,
            data.appName(),
            data.appLogoUrl(),
            data.appName(),
            data.jobTitle(),
            data.companyName(),
            data.appliedDate(),
            data.appName(),
            data.getApplicationStatusLink(),
            data.appName(),
            data.unsubscribeUrl(),
            data.privacyUrl()
        );

        return new EmailMessage(data.applicantEmail(), subject, htmlBody, userId);
    }
}