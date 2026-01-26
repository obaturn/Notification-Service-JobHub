package com.example.Notification_Service.Infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailSendLogRepository extends MongoRepository<EmailSendLog, String> {
}