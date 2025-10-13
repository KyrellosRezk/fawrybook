🌐 FawryBook — Social Platform
🧩 Overview

FawryBook is a full-stack social networking platform built using a microservices architecture.
It allows users to sign up, manage profiles, create posts, upload media, react to posts, and comment — all in a secure, distributed environment.

This repository includes:

🧠 User Management Service — Authentication, profiles, and relationships

📰 Post Management Service — Posts, reactions, and comments

📁 File Management Service — File upload, storage, and retrieval

💻 Frontend Web Application — Angular-based responsive UI

🧱 Architecture
frontend-service/
 └── Angular UI (User Interface Layer)

backend-source-code/
 ├── user-management-service/     → Handles authentication, user data, and relationships
 ├── post-management-service/     → Handles posts, likes, dislikes, and comments
 └── file-management-service/     → Handles file uploads, downloads, and storage

infrastructure/
 ├── PostgreSQL  → Database
 ├── Redis       → Caching and session storage
 ├── RabbitMQ    → Event-driven communication

⚙️ Tech Stack
Layer	Technology
Frontend	Angular 17, TailwindCSS, RxJS
Backend	Spring Boot 3, Java 17
Database	PostgreSQL
Messaging	RabbitMQ
Cache	Redis
Build Tool	Maven
Version Control	Git & GitHub
Deployment	Docker (optional)
🗂️ Services
1️⃣ User Management Service

Handles:

User registration & login (JWT-based)

Profile updates and photos

Friend requests (follow/unfollow)

Email verification

Runs on → http://localhost:8080/user-management/api/

📄 Config: application.properties

2️⃣ Post Management Service

Handles:

Create, edit, and delete posts

React to posts (like/dislike)

Add and view comments

Sync user and media data via RabbitMQ

Runs on → http://localhost:8081/post-management/api/

📄 Config: application.properties

3️⃣ File Management Service

Handles:

File upload (profile photos, post media)

File retrieval and deletion

Local storage management

JWT-protected access

Runs on → http://localhost:8082/file-management/api/

📄 Config: application.properties

4️⃣ Frontend Service

The Angular-based web client.

Displays posts, comments, and reactions.

Integrates with all backend APIs.

Handles authentication and media uploads.

Runs on → http://localhost:4200/

🚀 Running Locally
🧩 Requirements

Java 17

Node.js 18+

PostgreSQL running on port 5432

Redis running on port 6379

RabbitMQ running on port 5672

Maven

🪜 Steps
1️⃣ Start Dependencies

Make sure PostgreSQL, Redis, and RabbitMQ are running locally.

2️⃣ Start Each Backend Service
# In each service directory
mvn clean install
mvn spring-boot:run

3️⃣ Start Frontend
cd frontend-source-code
npm install
ng serve

🔐 Security

JWT authentication (RSA public/private key pair)

Password encryption using secret key

Role-based access for certain APIs

🧾 Folder Structure
fawrybook/
│
├── frontend-source-code/
│   └── Angular App
│
├── backend-source-code/
│   ├── user-management-service/
│   ├── post-management-service/
│   └── file-management-service/
│
├── scripts/
│   └── setup.sql (database initialization)
│
└── README.md

🧠 Key Features

✅ Modern, responsive UI

✅ Microservices with RabbitMQ messaging

✅ JWT-secured endpoints

✅ Redis caching for fast lookups

✅ File upload & retrieval system

✅ Comment and reaction management

✅ User profile synchronization

🧩 License

© 2025 FawryBook Platform — All rights reserved.
Created by Kyrellos Rezk
