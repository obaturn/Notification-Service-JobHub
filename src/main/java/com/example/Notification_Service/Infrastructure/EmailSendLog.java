package com.example.Notification_Service.Infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "email_send_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendLog {
    @Id
    private String id;
    private String userId;
    private String email;
    private boolean success;
    private String errorMessage;
    private LocalDateTime timestamp;
}