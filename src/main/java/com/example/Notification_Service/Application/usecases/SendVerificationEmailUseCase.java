package com.example.Notification_Service.Application.usecases;

import com.example.Notification_Service.Domain.VerificationEmailData;

public interface SendVerificationEmailUseCase {
    void sendVerificationEmail(VerificationEmailData data, String userId);
}