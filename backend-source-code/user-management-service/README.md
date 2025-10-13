# 🧩 User Management Service

The **User Management Service** is a core backend component built with **Spring Boot**.  
It handles user registration, authentication, email verification, password encryption, and user profile management.

---

## ⚙️ Configuration Overview

Below is the main configuration used in the `application.properties` file:

```properties
spring.application.name=user-management-service

# Application port
server.port=8080

# Application context
server.servlet.context-path=/user-management/api/

# PostgreSQL database configuration
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/user-management
spring.datasource.username=postgres
spring.datasource.password=1562000
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.max-lifetime=1800000
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
spring.jpa.hibernate.ddl-auto=none

# Redis
spring.data.redis.url=redis://localhost:6379/0
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
spring.data.redis.timeout=6000

# Flyway configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# Show health details
management.endpoint.health.show-details=always
management.endpoints.web.base-path=/actuator

# Password Secret Key
secret.key=fawrybook_password_secret_key

# JWT Secret Keys
jwt.rsa.private-key-path=classpath:keys/private.pem
jwt.rsa.public-key-path=classpath:keys/public.pem

# Gmail SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=kyrellos191480@gmail.com
spring.mail.password=emlp ebks lgvy wxto
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.listener.simple.acknowledge-mode=auto

# RabbitMQ Exchange
rabbitmq.exchange.follow=follow
```

---

## 🚀 Features

- User registration and login
- JWT authentication with RSA encryption
- Email verification using Gmail SMTP
- Redis caching for performance optimization
- RabbitMQ integration for event-driven communication
- Flyway migrations for database versioning
- RESTful API with Spring Boot

---

## 🧠 Technologies Used

- **Java 17**
- **Spring Boot 3+**
- **PostgreSQL**
- **Redis**
- **RabbitMQ**
- **Flyway**
- **Spring Security + JWT**
- **Spring Mail (Gmail SMTP)**

---

## 🏁 Running the Service

### ✅ Prerequisites
- Java 17 installed  
- PostgreSQL running on port **5432**  
- Redis running on port **6379**  
- RabbitMQ running on port **5672**  

### 🧩 Steps
1. Clone the repository  
   ```bash
   git clone https://github.com/your-repo/user-management-service.git
   cd user-management-service
   ```

2. Configure `application.properties` with your credentials.

3. Run Flyway migrations (automatically runs at startup).

4. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the API at:  
   👉 `http://localhost:8080/user-management/api/`

---

## 🔒 Security Notes

- Never expose your real Gmail password or JWT private key in public repositories.  
- Use environment variables or **Spring Cloud Vault** for secret management in production.

---

## 🧑‍💻 Author
**Kyrellos Rezk**  
📧 kyrellos191480@gmail.com  
