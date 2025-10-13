🧩 Overview

The File Management Service is a microservice in the FawryBook ecosystem responsible for managing file uploads, downloads, and secure file storage.
It provides APIs for uploading user profile images, post attachments, and other assets while maintaining security through JWT authentication.

⚙️ Configuration
📘 application.properties
spring.application.name=file-management-service

# Application port
server.port=8082

# Application context
server.servlet.context-path=/file-management/api/

# Show health details
management.endpoint.health.show-details=always

# Base path (default is /actuator)
management.endpoints.web.base-path=/actuator

# JWT Secret key
jwt.rsa.public-key-path=classpath:keys/public.pem

# Storage path
storage.path=D:/fawrybook/backend-source-code/file-management-service/storage/

🗂️ Features

Upload and retrieve files (images, videos, documents).

Store files locally under the configured path.

Secure file operations using JWT verification.

Configurable storage base directory.

Actuator endpoints for service monitoring.

🧪 API Endpoints
Method	Endpoint	Description
POST	/upload	Upload a file to the storage directory
GET	/get/{path}	Retrieve a stored file
DELETE	/delete/{path}	Delete a stored file
🛠️ Tech Stack

Spring Boot (v3+)

JWT Security

Local File Storage

Actuator

🚀 Running the Application
1️⃣ Prerequisites

Java 17+

Maven

Postman or curl for testing

2️⃣ Build and Run
mvn clean install
mvn spring-boot:run

3️⃣ Access URLs

Base URL → http://localhost:8082/file-management/api/

Health Check → http://localhost:8082/file-management/api/actuator/health

🔐 Security

Requires a valid JWT token signed using the private key from user-management-service.

The public key (public.pem) is used to verify tokens in this service.

🧾 License

© 2025 FawryBook Platform — All rights reserved.