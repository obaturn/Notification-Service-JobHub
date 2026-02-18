# Notification Service

This is a Spring Boot-based notification service designed to send verification emails in response to user registration events. The service follows Clean Architecture principles and implements robust error handling, resilience patterns, and event-driven communication.

## Architecture

The project is structured using Clean Architecture with four main layers:

- **Domain**: Contains business logic and entities
- **Application**: Contains use cases and application ports
- **Infrastructure**: Contains external concerns like databases, email providers, and messaging
- **Presentation**: Contains controllers and event listeners

## Technologies Used

### Framework and Language
- **Java 17**: Programming language
- **Spring Boot 4.0.2**: Main framework for building the application
- **Maven**: Build tool and dependency management

### Messaging and Event-Driven Architecture
- **Apache Kafka**: Used for event-driven communication. The service consumes `UserRegistered` events from the `user-events` topic.
- **Spring Kafka**: Integration with Kafka for consumer and producer configurations.

### Email Providers
- **JavaMail (Spring Boot Mail)**: Primary email provider using SMTP (configured for Gmail)
- **SendGrid**: Alternative email provider, conditionally enabled when API key is provided

### Resilience and Fault Tolerance
- **Resilience4j**: Circuit breaker pattern to prevent cascading failures
- **Spring Retry**: Retry mechanism for transient failures

### Data Storage
- **MongoDB**: Used for logging email send attempts and outcomes

### Utilities
- **Lombok**: Reduces boilerplate code with annotations
- **Jackson**: JSON serialization/deserialization with Java Time support
- **AspectJ**: For AOP support in retry mechanisms

## How Technologies Are Used

### Spring Boot
- Provides the application framework with auto-configuration
- Used for dependency injection, configuration management, and web MVC setup
- `@SpringBootApplication` enables component scanning and auto-configuration
- `@EnableRetry` enables retry functionality across the application

### Kafka
- **Consumer Configuration**: Manual acknowledgment mode for reliable message processing
- **Error Handling**: Dead letter topics (DLT) for failed messages with exponential backoff retries
- **Event Processing**: Listens to `user-events` topic, processes `UserRegistered` events, and triggers email sending
- **Serialization**: String-based serialization for messages

### Email Sending
- **JavaMail Provider**: Primary implementation using SMTP with Gmail
  - Configured with authentication and STARTTLS
  - Supports HTML email content
  - Integrated with circuit breaker and retry
- **SendGrid Provider**: Alternative implementation using SendGrid API
  - Conditionally enabled based on API key presence
  - Uses HTTP API calls for email delivery

### Resilience4j Circuit Breaker
- **Configuration**: 50% failure rate threshold, 30-second wait in open state
- **Sliding Window**: Considers last 10 calls
- **Fallback**: Logs failures without throwing exceptions when circuit is open
- **Purpose**: Prevents overwhelming downstream email services during outages

### Spring Retry
- **Retry Logic**: Up to 3 attempts for RuntimeExceptions
- **Integration**: Works alongside circuit breaker for comprehensive fault tolerance

### MongoDB
- **Logging**: Stores email send logs including success/failure status, timestamps, and error messages
- **Repository Pattern**: Uses Spring Data MongoDB for data access

### Lombok
- **Code Reduction**: Eliminates boilerplate getters, setters, constructors, and logging
- **Annotations**: `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Slf4j` used throughout

### Jackson
- **ObjectMapper Bean**: Custom configuration with JavaTimeModule for LocalDateTime serialization
- **Event Deserialization**: Converts JSON Kafka messages to domain objects

## Configuration

Key configuration properties in `application.properties`:

- **Kafka**: Bootstrap servers, consumer group, deserializers
- **MongoDB**: Connection URI
- **Email**: SMTP settings for JavaMail, optional SendGrid API key
- **Application**: Custom properties for email templates (app name, URLs, etc.)
- **Circuit Breaker**: Resilience4j settings for email service

## Workflow

1. **Event Reception**: Kafka listener receives `UserRegistered` events
2. **Data Preparation**: Creates verification email data with user details and app configuration
3. **Email Sending**: Use case orchestrates email creation and sending
4. **Provider Selection**: Uses primary JavaMail provider, falls back to SendGrid if configured
5. **Resilience**: Circuit breaker and retry protect against failures
6. **Logging**: All attempts logged to MongoDB for monitoring and debugging

## Why These Technologies?

- **Spring Boot**: Rapid development, extensive ecosystem, production-ready features
- **Kafka**: Scalable event-driven architecture, decoupling of services
- **Multiple Email Providers**: Redundancy and flexibility for different environments
- **Resilience4j + Spring Retry**: Fault tolerance in distributed systems
- **MongoDB**: Flexible schema for logging, easy integration with Spring
- **Lombok**: Cleaner code, reduced maintenance
- **Clean Architecture**: Testable, maintainable, and scalable codebase

## Running the Application

1. Ensure Java 17, Maven, and required infrastructure (Kafka, MongoDB) are running
2. Configure environment variables for Kafka and optional SendGrid
3. Run with `mvn spring-boot:run` or deploy the JAR

## Testing

- Unit tests for use cases
- Integration tests for email sending (mocked)
- Spring Boot test framework used