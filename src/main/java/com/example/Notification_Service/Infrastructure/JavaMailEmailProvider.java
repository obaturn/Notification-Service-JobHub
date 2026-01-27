package com.example.Notification_Service.Infrastructure;

import com.example.Notification_Service.Application.ports.EmailSender;
import com.example.Notification_Service.Domain.EmailMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Primary
public class JavaMailEmailProvider implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(JavaMailEmailProvider.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final Optional<EmailSendLogRepository> logRepository;

    public JavaMailEmailProvider(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromEmail,
            Optional<EmailSendLogRepository> logRepository) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.logRepository = logRepository;
    }

    @Override
    @CircuitBreaker(name = "emailService", fallbackMethod = "sendEmailFallback")
    @Retryable(value = RuntimeException.class, maxAttempts = 3)
    public void sendEmail(EmailMessage emailMessage) {
        logger.info("Attempting to send email to userId: {}", emailMessage.userId());
        EmailSendLog log = new EmailSendLog(null, emailMessage.userId(), emailMessage.to(), false, null, LocalDateTime.now());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailMessage.to());
            helper.setSubject(emailMessage.subject());
            helper.setText(emailMessage.body(), true); // true = HTML content

            mailSender.send(message);
            log.setSuccess(true);
            logger.info("Email sent successfully to userId: {}", emailMessage.userId());

        } catch (Exception e) {
            log.setSuccess(false);
            log.setErrorMessage(e.getMessage());
            logger.error("Error sending email to userId: {}, error: {}", emailMessage.userId(), e.getMessage());
            throw new RuntimeException("Error sending email", e);
        } finally {
            logRepository.ifPresent(repo -> repo.save(log));
        }
    }

    /**
     * Fallback method called when circuit breaker is OPEN
     * Logs the failure and continues without throwing exception
     */
    public void sendEmailFallback(EmailMessage emailMessage, Throwable throwable) {
        logger.warn("Circuit breaker is OPEN for email service. Skipping email send for userId: {} due to: {}",
                   emailMessage.userId(), throwable.getMessage());

        // Still log the attempt as failed
        EmailSendLog log = new EmailSendLog(null, emailMessage.userId(), emailMessage.to(), false,
                                          "Circuit breaker open: " + throwable.getMessage(), LocalDateTime.now());
        logRepository.ifPresent(repo -> repo.save(log));
    }
}