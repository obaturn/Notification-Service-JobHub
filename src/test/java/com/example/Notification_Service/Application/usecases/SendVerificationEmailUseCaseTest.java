package com.example.Notification_Service.Application.usecases;

import com.example.Notification_Service.Application.ports.EmailSender;
import com.example.Notification_Service.Domain.EmailMessage;
import com.example.Notification_Service.Domain.EmailNotificationService;
import com.example.Notification_Service.Domain.VerificationEmailData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendVerificationEmailUseCaseTest {

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private SendVerificationEmailUseCaseImpl sendVerificationEmailUseCase;

    @Test
    void shouldSendVerificationEmail() {
        // Given
        String userId = "user123";
        VerificationEmailData data = new VerificationEmailData(
            "John",
            "test@example.com",
            "token123",
            "http://localhost:3000",
            "JobHub",
            "https://jobhub.com/logo.png",
            "http://localhost:3000/support",
            24
        );
        EmailMessage emailMessage = new EmailMessage("test@example.com", "Verify Your Email", "Body", userId);

        when(emailNotificationService.composeVerificationEmail(data, userId)).thenReturn(emailMessage);

        // When
        sendVerificationEmailUseCase.sendVerificationEmail(data, userId);

        // Then
        verify(emailNotificationService).composeVerificationEmail(data, userId);
        verify(emailSender).sendEmail(emailMessage);
    }
}