package com.example.Notification_Service.Infrastructure;

import com.example.Notification_Service.Application.ports.EmailSender;
import com.example.Notification_Service.Domain.EmailMessage;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class SendGridEmailProvider implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailProvider.class);

    private final SendGrid sendGrid;
    private final Optional<EmailSendLogRepository> logRepository;

    public SendGridEmailProvider(@Value("${sendgrid.api.key}") String apiKey, Optional<EmailSendLogRepository> logRepository) {
        this.sendGrid = new SendGrid(apiKey);
        this.logRepository = logRepository;
    }

    @Override
    @Retryable(value = RuntimeException.class, maxAttempts = 3)
    public void sendEmail(EmailMessage emailMessage) {
        logger.info("Attempting to send email to userId: {}", emailMessage.userId());
        EmailSendLog log = new EmailSendLog(null, emailMessage.userId(), emailMessage.to(), false, null, LocalDateTime.now());
        try {
            Email from = new Email("noreply@yourapp.com");
            Email to = new Email(emailMessage.to());
            Content content = new Content("text/plain", emailMessage.body());
            Mail mail = new Mail(from, emailMessage.subject(), to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.setSuccess(true);
                logger.info("Email sent successfully to userId: {}", emailMessage.userId());
            } else {
                log.setSuccess(false);
                log.setErrorMessage("Status: " + response.getStatusCode());
                logger.error("Failed to send email to userId: {}, status: {}", emailMessage.userId(), response.getStatusCode());
                throw new RuntimeException("Failed to send email: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.setSuccess(false);
            log.setErrorMessage(e.getMessage());
            logger.error("Error sending email to userId: {}, error: {}", emailMessage.userId(), e.getMessage());
            throw new RuntimeException("Error sending email", e);
        } finally {
            logRepository.ifPresent(repo -> repo.save(log));
        }
    }
}