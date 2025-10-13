# 📝 Post Service (Backend)

## 📘 Overview
The **Post Service** is a Spring Boot backend microservice responsible for managing user posts, comments, media, and social interactions in the application ecosystem.  
It interacts with PostgreSQL for persistent storage, Redis for caching, RabbitMQ for asynchronous messaging, and Flyway for database migrations.

---

## ⚙️ Technologies Used
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA (Hibernate)**
- **PostgreSQL**
- **Redis**
- **RabbitMQ**
- **Flyway**
- **JWT Authentication**
- **Spring Actuator** (for health monitoring)

---

## 🚀 Running the Application

### 1️⃣ Prerequisites
Make sure you have installed:
- **Java 17**
- **Maven 3.9+**
- **PostgreSQL 14+**
- **Redis 6+**
- **RabbitMQ 3.10+**

---

### 2️⃣ Configure Database
Create a PostgreSQL database named `post_management`:
```sql
CREATE DATABASE post_management;
```

Then update your credentials inside `application.properties` if needed.

---

### 3️⃣ Run Redis and RabbitMQ
If you use Docker, you can quickly start them with:
```bash
docker run -d --name redis -p 6379:6379 redis
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
```

---

### 4️⃣ Build and Run
Use Maven to package and run the service:

```bash
mvn clean install
java -jar target/post-service-0.0.1-SNAPSHOT.jar
```

The API will be available at:
```
http://localhost:8081/post-management/api/
```

---

## ⚙️ Configuration Details

### 🔧 `application.properties`
```properties
spring.application.name=post-service

server.port=8081
server.servlet.context-path=/post-management/api/

spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/post_management
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

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.listener.simple.acknowledge-mode=auto

spring.data.redis.url=redis://localhost:6379/0
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
spring.data.redis.timeout=6000

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

management.endpoint.health.show-details=always
management.endpoints.web.base-path=/actuator

jwt.rsa.public-key-path=classpath:keys/public.pem

rabbit.following.queue=following
rabbit.unfollowing.queue=unfollowing
```

---

## 🧩 Key Features
✅ Post creation, deletion, and update  
✅ Comment management  
✅ Media upload support  
✅ Redis caching for performance  
✅ RabbitMQ integration for event-driven messaging  
✅ Flyway migrations for DB versioning  
✅ JWT authentication support  
✅ Actuator endpoints for monitoring  

---

## 📁 Project Structure
```
post-service/
 ┣ src/
 ┃ ┣ main/
 ┃ ┃ ┣ java/com/example/postservice/
 ┃ ┃ ┣ resources/
 ┃ ┃ ┃ ┣ application.properties
 ┃ ┃ ┃ ┗ db/migration/
 ┣ target/
 ┣ pom.xml
 ┗ README.md
```

---

## 📊 Actuator Health Check
Check application health:
```
GET http://localhost:8081/post-management/api/actuator/health
```

---

## 🔐 Security
- JWT-based authentication
- RSA public key for verifying tokens

---

## 📨 Message Queues
- `following` queue: listens for follow events
- `unfollowing` queue: listens for unfollow events

---

## 🧠 Notes
- Ensure your public key (`public.pem`) is located under `src/main/resources/keys/`
- Keep sensitive credentials outside version control in production
- Consider using **Spring Cloud Vault** or environment variables for secret management

---

## 👨‍💻 Author
**Kyrellos Rezk**  
Backend Developer | Spring Boot, PostgreSQL, Redis, RabbitMQ

