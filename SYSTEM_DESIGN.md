# JobHub Notification Service - System Design Document

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [System Components](#system-components)
4. [Data Flow](#data-flow)
5. [Technology Stack](#technology-stack)
6. [Kafka Configuration](#kafka-configuration)
7. [Email Templates](#email-templates)
8. [Error Handling & Resilience](#error-handling--resilience)
9. [Configuration](#configuration)
10. [API Endpoints & Topics](#api-endpoints--topics)
11. [Database Schema](#database-schema)
12. [Security Considerations](#security-considerations)
13. [Future Enhancements](#future-enhancements)

---

## 1. Overview

The **JobHub Notification Service** is a microservice responsible for sending transactional emails to users of the JobHub platform. It consumes events from Apache Kafka and delivers personalized emails using a robust, fault-tolerant architecture.

### Key Responsibilities
- Send verification emails to new users upon registration
- Send congratulation emails to applicants when they submit job applications
- Log all email send attempts for auditing and monitoring
- Handle failures gracefully with circuit breaker pattern

---

## 2. Architecture

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            EXTERNAL SERVICES                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│  User Service          │  Application Service       │  Email Providers       │
│  (user-events)        │  (application-events)      │  - Gmail (JavaMail)    │
│                       │                            │  - SendGrid (alt)      │
└───────────┬───────────┴────────────┬─────────────┴───────────┬─────────────┘
            │                        │                         │
            ▼                        ▼                         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          KAFKA MESSAGE BROKER                              │
├─────────────────────────────────────────────────────────────────────────────┤
│  Topics:                                                                   │
│  ├── user-events        (UserRegistered events)                             │
│  └── application-events (ApplicationSubmitted events)                       │
└─────────────────────────────────────┬───────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      NOTIFICATION SERVICE (Port 8082)                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     PRESENTATION LAYER                               │   │
│  │  ┌─────────────────────────┐  ┌──────────────────────────────────┐  │   │
│  │  │  KafkaEventListener    │  │  ApplicationEventConsumer        │  │   │
│  │  │  (user-events topic)   │  │  (application-events topic)      │  │   │
│  │  └───────────┬─────────────┘  └──────────────┬───────────────────┘  │   │
│  └──────────────┼────────────────────────────────┼───────────────────────┘   │
│                 │                                 │                          │
│                 ▼                                 ▼                          │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      APPLICATION LAYER                              │   │
│  │  ┌─────────────────────────┐  ┌──────────────────────────────────┐  │   │
│  │  │  SendVerificationEmail  │  │  SendCongratulationsEmail         │  │   │
│  │  │  UseCase                │  │  UseCase                         │  │   │
│  │  └───────────┬─────────────┘  └──────────────┬───────────────────┘  │   │
│  └──────────────┼────────────────────────────────┼───────────────────────┘   │
│                 │                                 │                          │
│                 ▼                                 ▼                          │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        DOMAIN LAYER                                 │   │
│  │  ┌─────────────────────────┐  ┌──────────────────────────────────┐  │   │
│  │  │  EmailNotificationService│  │  EmailMessage                   │  │   │
│  │  │  (Template Engine)      │  │  (Email DTO)                    │  │   │
│  │  └─────────────────────────┘  └──────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   INFRASTRUCTURE LAYER                             │   │
│  │  ┌─────────────────┐  ┌──────────────┐  ┌─────────────────────┐  │   │
│  │  │ JavaMail Provider│  │SendGrid Provider│ │  MongoDB Repository │  │   │
│  │  │ (Primary)        │  │ (Alternative) │  │  (EmailSendLog)    │  │   │
│  │  └─────────────────┘  └──────────────┘  └─────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer | Responsibility |
|-------|----------------|
| **Presentation** | Kafka consumers, event listeners |
| **Application** | Use cases, business logic orchestration |
| **Domain** | Email templates, data models, notification logic |
| **Infrastructure** | Email providers, database, Kafka configuration |

---

## 3. System Components

### 3.1 Presentation Layer

#### KafkaEventListener
- **Purpose**: Listens to `user-events` topic for user registration
- **Group ID**: `notification-service`
- **Acknowledgment**: Manual
- **Event Types Handled**: `UserRegistered`

#### ApplicationEventConsumer
- **Purpose**: Listens to `application-events` topic for job applications
- **Group ID**: `notification-service`
- **Acknowledgment**: Manual
- **Event Types Handled**: `APPLICATION_SUBMITTED`

### 3.2 Application Layer

#### SendVerificationEmailUseCase
- **Interface**: Defines the contract for sending verification emails
- **Implementation**: `SendVerificationEmailUseCaseImpl`

#### SendCongratulationsEmailUseCase
- **Interface**: Defines the contract for sending congratulation emails
- **Implementation**: `SendCongratulationsEmailUseCaseImpl`

### 3.3 Domain Layer

#### EmailNotificationService
- **Interface**: Defines email composition methods
- **Implementation**: `EmailNotificationServiceImpl`

#### EmailMessage
- **Purpose**: DTO representing an email message
- **Fields**: `to`, `subject`, `body`, `userId`

#### Domain Models
| Model | Purpose |
|-------|---------|
| `UserRegisteredEvent` | Kafka event for user registration |
| `ApplicationEventData` | Kafka event for job application |
| `VerificationEmailData` | Data for verification email composition |
| `ApplicationEmailData` | Data for congratulation email composition |

### 3.4 Infrastructure Layer

#### JavaMailEmailProvider (Primary)
- **Purpose**: Sends emails via Gmail SMTP
- **Features**: Circuit breaker, retry mechanism, logging

#### SendGridEmailProvider (Alternative)
- **Purpose**: Alternative email provider via SendGrid API

#### EmailSendLogRepository
- **Database**: MongoDB
- **Purpose**: Logs all email send attempts for auditing

#### KafkaConfig
- **Purpose**: Configures Kafka consumer/producer factories
- **Features**: Manual acknowledgment, dead letter topic, error handling

---

## 4. Data Flow

### 4.1 User Registration Flow

```
┌──────────────┐     ┌──────────────┐     ┌─────────────────────┐     ┌──────────────┐
│   User       │     │  User       │     │      Kafka          │     │ Notification │
│   Service    │────▶│  Service    │────▶│    (user-events)    │────▶│   Service    │
└──────────────┘     └──────────────┘     └─────────────────────┘     └──────────────┘
                                                                              │
                                                                              ▼
                                                                    ┌──────────────┐
                                                                    │   Gmail      │
                                                                    │  (JavaMail)  │
                                                                    └──────────────┘
                                                                              │
                                                                              ▼
                                                                    ┌──────────────┐
                                                                    │   User       │
                                                                    │   Email      │
                                                                    └──────────────┘
```

**Steps:**
1. User registers via User Service
2. User Service publishes `UserRegistered` event to Kafka `user-events` topic
3. `KafkaEventListener` consumes the event
4. `SendVerificationEmailUseCase` composes and sends verification email
5. Email is delivered to user's inbox via Gmail SMTP

### 4.2 Job Application Flow

```
┌──────────────┐     ┌──────────────┐     ┌─────────────────────┐     ┌──────────────┐
│   Job       │     │ Application  │     │      Kafka          │     │ Notification │
│   Seeker    │────▶│  Service     │────▶│ (application-events)│────▶│   Service    │
└──────────────┘     └──────────────┘     └─────────────────────┘     └──────────────┘
                                                                              │
                                                                              ▼
                                                                    ┌──────────────┐
                                                                    │   Gmail      │
                                                                    │  (JavaMail)  │
                                                                    └──────────────┘
                                                                              │
                                                                              ▼
                                                                    ┌──────────────┐
                                                                    │  Applicant   │
                                                                    │   Email      │
                                                                    └──────────────┘
```

**Steps:**
1. Job seeker submits application via Application Service
2. Application Service publishes `APPLICATION_SUBMITTED` event to Kafka `application-events` topic
3. `ApplicationEventConsumer` consumes the event
4. `SendCongratulationsEmailUseCase` composes and sends congratulation email
5. Email is delivered to applicant's inbox

---

## 5. Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 4.0.2 |
| **Build Tool** | Maven | 3.x |
| **Message Broker** | Apache Kafka | 4.1.1 |
| **Database** | MongoDB | - |
| **Email (Primary)** | JavaMail (Gmail SMTP) | - |
| **Email (Alternative)** | SendGrid | - |
| **Circuit Breaker** | Resilience4j | - |
| **Retry** | Spring Retry | - |
| **Serialization** | Jackson | - |

### Dependencies (pom.xml Key Dependencies)
```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- MongoDB -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- Circuit Breaker -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>

<!-- Email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

---

## 6. Kafka Configuration

### Consumer Configuration
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=notification-service
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

### Container Factories
| Factory | Purpose | Ack Mode |
|---------|---------|----------|
| `kafkaListenerContainerFactory` | Default consumer | Auto |
| `manualAckContainerFactory` | Manual acknowledgment | MANUAL_IMMEDIATE |

### Error Handling
- **Retries**: 3 attempts with exponential backoff
- **Dead Letter Topic**: Failed messages sent to DLT after retries exhausted
- **Logging**: All retry attempts logged

---

## 7. Email Templates

### 7.1 Verification Email
**Subject**: `Verify Your Email Address - Welcome to JobHub!`

**Template Features**:
- JobHub logo with gradient header
- Personalized greeting with user's first name
- Primary CTA button for email verification
- Backup link for manual verification
- Expiration warning (24 hours)
- Platform features list
- Footer with support links

**Styling**:
- Header gradient: `#667eea` to `#764ba2`
- Font: Apple System, Segoe UI, Roboto
- Responsive design (mobile-friendly)

### 7.2 Congratulation Email
**Subject**: `🎉 Application Submitted! {jobTitle} at {companyName}`

**Template Features**:
- JobHub logo with blue gradient header
- 🎉 congratulations banner
- Application details card:
  - Job Title
  - Company Name
  - Applied Date
- Next steps information
- CTA button to view application status
- Unsubscribe and privacy policy links

**Styling**:
- Header gradient: `#0061f2` to `#00c6f7`
- Success indicator: Green left border
- Professional card-based layout

---

## 8. Error Handling & Resilience

### 8.1 Circuit Breaker Pattern
```properties
resilience4j.circuitbreaker.instances.emailService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.emailService.wait-duration-in-open-state=30s
resilience4j.circuitbreaker.instances.emailService.sliding-window-size=10
resilience4j.circuitbreaker.instances.emailService.minimum-number-of-calls=5
resilience4j.circuitbreaker.instances.emailService.permitted-number-of-calls-in-half-open-state=3
```

**States**:
| State | Behavior |
|-------|----------|
| CLOSED | Normal operation, failures tracked |
| OPEN | Calls fail fast, fallback triggered |
| HALF_OPEN | Test calls allowed to check recovery |

### 8.2 Retry Mechanism
- **Attempts**: 3 retries
- **Trigger**: RuntimeException
- **Logging**: Each retry attempt logged

### 8.3 Fallback Strategy
When circuit breaker is OPEN:
- Email send is skipped
- Failure is logged to MongoDB
- No exception thrown to caller

---

## 9. Configuration

### Application Properties
```properties
# Service
spring.application.name=Notification-Service
server.port=8082

# MongoDB
spring.data.mongodb.uri=mongodb+srv://...

# Kafka
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
spring.kafka.consumer.group-id=notification-service

# App Config
app.name=JobHub
app.logo-url=https://jobhub.com/logo.png
app.url=http://localhost:3000
app.support-url=http://localhost:3000/support
app.token-expiry-hours=24
app.unsubscribe-url=http://localhost:3000/unsubscribe
app.privacy-url=http://localhost:3000/privacy

# Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 10. API Endpoints & Topics

### Kafka Topics
| Topic | Event Type | Description |
|-------|------------|--------------|
| `user-events` | UserRegistered | New user registration |
| `application-events` | APPLICATION_SUBMITTED | Job application submitted |

### REST Endpoints
The service exposes Spring Boot Actuator endpoints (if enabled):
- `/actuator/health` - Health check
- `/actuator/info` - Application info

---

## 11. Database Schema

### EmailSendLog Collection (MongoDB)
```json
{
  "_id": "ObjectId",
  "userId": "string",
  "recipient": "string",
  "success": "boolean",
  "errorMessage": "string | null",
  "timestamp": "datetime"
}
```

**Sample Documents**:
```json
{
  "_id": "507f1f77bcf86cd799439011",
  "userId": "user-123",
  "recipient": "john@example.com",
  "success": true,
  "errorMessage": null,
  "timestamp": "2026-02-15T10:30:00Z"
}
```

```json
{
  "_id": "507f1f77bcf86cd799439012",
  "userId": "user-456",
  "recipient": "jane@example.com",
  "success": false,
  "errorMessage": "Circuit breaker open: Connection timeout",
  "timestamp": "2026-02-15T10:31:00Z"
}
```

---

## 12. Security Considerations

### Email Credentials
- Gmail app-specific password recommended (not account password)
- Store credentials in environment variables or secrets manager
- Never commit credentials to version control

### Kafka Security
- Consider SSL/TLS for production
- Use SASL authentication
- Implement topic authorization

### Data Privacy
- Email logs stored temporarily
- PII handled according to GDPR requirements
- Unsubscribe links mandatory in all emails

---

## 13. Future Enhancements

### Planned Features
| Feature | Description |
|---------|-------------|
| **Email Templates Storage** | Store templates in database for dynamic updates |
| **Template Variables API** | REST API to manage email templates |
| **Batch Sending** | Support for bulk email campaigns |
| **Email Tracking** | Open/click tracking |
| **Multi-language Support** | i18n for email content |
| **Webhooks** | HTTP callbacks for email events |
| **SMS Notifications** | Add SMS alongside email |
| **Push Notifications** | Mobile push notifications |

### Scaling Considerations
- Horizontal scaling via multiple service instances
- Kafka consumer group for load balancing
- Connection pooling for email providers
- Caching frequently used data

---

## Appendix: Class Diagram

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                           PRESENTATION LAYER                                   │
├────────────────────────────────────────────────────────────────────────────────┤
│  @Component                                                                          │
│  ┌─────────────────────────┐     ┌─────────────────────────────────────────┐  │
│  │    KafkaEventListener   │     │     ApplicationEventConsumer           │  │
│  │  ─────────────────────  │     │  ───────────────────────────────────  │  │
│  │  - sendVerification    │     │  - sendCongratulationsEmail            │  │
│  │    EmailUseCase        │     │    UseCase                            │  │
│  │  - objectMapper        │     │  - objectMapper                        │  │
│  └───────────┬─────────────┘     └──────────────┬────────────────────────┘  │
└──────────────┼──────────────────────────────────┼────────────────────────────┘
               │                                   │
               ▼                                   ▼
┌────────────────────────────────────────────────────────────────────────────────┐
│                            APPLICATION LAYER                                   │
├────────────────────────────────────────────────────────────────────────────────┤
│  @Service                        │              @Service                        │
│  ┌─────────────────────────┐    │    ┌──────────────────────────────────┐     │
│  │   SendVerification      │    │    │     SendCongratulations          │     │
│  │   EmailUseCase         │    │    │     EmailUseCase                 │     │
│  │  ────────────────────  │    │    │  ──────────────────────────────  │     │
│  │  + sendVerification    │    │    │  + sendCongratulationsEmail       │     │
│  │    Email()             │    │    │    ()                            │     │
│  └───────────┬─────────────┘    │    └──────────────┬──────────────────┘     │
└──────────────┼───────────────────┼───────────────────┼──────────────────────────┘
               │                   │                   │
               ▼                   ▼                   ▼
┌────────────────────────────────────────────────────────────────────────────────┐
│                              DOMAIN LAYER                                       │
├────────────────────────────────────────────────────────────────────────────────┤
│  <<interface>>                  │              <<interface>>                    │
│  ┌──────────────────────┐       │       ┌─────────────────────────────────┐    │
│  │ EmailNotification   │       │       │         EmailSender             │    │
│  │ Service             │       │       │  ─────────────────────────────── │    │
│  │ ──────────────────  │       │       │  + sendEmail(EmailMessage)     │    │
│  │ + composeVerification│      │       └───────────────┬─────────────────┘    │
│  │   Email()           │       │                       │                      │
│  │ + composeCongratulations│   │                       ▼                      │
│  │   Email()           │       │       ┌─────────────────────────────────┐    │
│  └─────────┬────────────┘       │       │         EmailMessage          │    │
│            │                     │       │  ───────────────────────────  │    │
│            ▼                     │       │  - to: String                 │    │
│  ┌──────────────────────┐        │       │  - subject: String            │    │
│  │ EmailNotification   │        │       │  - body: String               │    │
│  │ ServiceImpl         │        │       │  - userId: String            │    │
│  │  ──────────────────  │        │       └────────────────────────────┘    │
│  │ + composeVerification│        │                                             │
│  │   Email()           │        │       ┌─────────────────────────────────┐    │
│  │ + composeCongratulations│    │       │   Domain Events                │    │
│  │   Email()           │        │       │  ───────────────────────────  │    │
│  └──────────────────────┘        │       │  • UserRegisteredEvent        │    │
│                                  │       │  • ApplicationEventData       │    │
└──────────────────────────────────┴───────┴─────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────────┐
│                          INFRASTRUCTURE LAYER                                  │
├────────────────────────────────────────────────────────────────────────────────┤
│  @Component @Primary           │              @Component                         │
│  ┌─────────────────────┐       │       ┌──────────────────────────────────┐   │
│  │ JavaMailEmailProvider│      │       │     SendGridEmailProvider        │   │
│  │ ───────────────────  │       │       │  ──────────────────────────────  │   │
│  │ + sendEmail()       │       │       │  + sendEmail()                   │   │
│  │ + sendEmailFallback│       │       └──────────────────────────────────┘   │
│  └──────────┬──────────┘                                                     │
│             │                                                                   │
│             ▼                                                                   │
│  ┌─────────────────────────────────────────┐    ┌──────────────────────────┐   │
│  │        KafkaConfig                      │    │   EmailCircuitBreaker   │   │
│  │  ─────────────────────────────────────  │    │   Config                │   │
│  │  + consumerFactory()                   │    │  ──────────────────────  │   │
│  │  + producerFactory()                   │    │   failure-rate: 50%    │   │
│  │  + kafkaListenerContainerFactory()    │    │   wait: 30s             │   │
│  │  + manualAckContainerFactory()         │    │   retry: 3              │   │
│  └─────────────────────────────────────────┘    └──────────────────────────┘   │
│                                                                                │
│  ┌─────────────────────────────────────────┐    ┌──────────────────────────┐   │
│  │      EmailSendLog (MongoDB)            │    │   EmailSendLogRepository │   │
│  │  ─────────────────────────────────────  │    │  ──────────────────────  │   │
│  │  - id: ObjectId                        │    │  + save()                │   │
│  │  - userId: String                     │    │  + findByUserId()        │   │
│  │  - recipient: String                  │    │  + findBySuccess()       │   │
│  │  - success: Boolean                   │    └──────────────────────────┘   │
│  │  - errorMessage: String                                               │
│  │  - timestamp: LocalDateTime                                          │
│  └─────────────────────────────────────────┘                                │
└────────────────────────────────────────────────────────────────────────────────┘
```

---

*Document Version: 1.0*  
*Last Updated: February 2026*  
*Author: System Architecture Team*
