
# 🖥️ FawryBook Frontend Service

## 📘 Overview
This project is the **frontend service** for the FawryBook social application — built using **Angular 17**, **Tailwind CSS**, and **Angular Material**.  
It connects to the backend microservices (`user-management-service`, `post-management-service`, etc.) and provides a modern, responsive, and real-time user experience.

---

## 🚀 Features
- 🧑‍🤝‍🧑 User Authentication & Profiles  
- 📝 Post creation, editing, and deletion  
- 💬 Comments and reactions (like/dislike)  
- 👥 Friend requests (approve/decline)  
- 🖼️ Media upload and preview  
- 🔄 Dynamic content loading with pagination  
- 🌙 Dark UI with Tailwind CSS  

---

## 🏗️ Project Structure
```
frontend-service/
│
├── src/
│   ├── app/
│   │   ├── auth/               # Authentication components
│   │   ├── feed/               # Main feed with posts and reactions
│   │   ├── profile/            # User profile pages
│   │   ├── services/           # HttpClient-based services (Post, Comment, File, etc.)
│   │   ├── payloads/           # DTOs and response/requests models
│   │   └── shared/             # Shared UI components
│   ├── assets/                 # Static images and icons
│   └── environments/           # Environment config files
│
├── tailwind.config.js
├── package.json
├── angular.json
└── README.md
```

---

## ⚙️ Setup Instructions

### 1️⃣ Prerequisites
Ensure you have the following installed:
- [Node.js 18+](https://nodejs.org/)
- [Angular CLI 17+](https://angular.io/cli)
- npm or yarn package manager

### 2️⃣ Installation
```bash
git clone https://github.com/your-org/frontend-service.git
cd frontend-service
npm install
```

### 3️⃣ Development Server
Start the local Angular server:
```bash
ng serve
```
Visit the app at 👉 [http://localhost:4200](http://localhost:4200)

### 4️⃣ Build for Production
```bash
ng build --configuration production
```

### 5️⃣ Environment Configuration
Update the backend API URLs in `src/environments/environment.ts`:
```ts
export const environment = {
  production: false,
  apiUrls: {
    user: 'http://localhost:8080/user-management/api/v1',
    post: 'http://localhost:8081/post-management/api/v1'
  }
};
```

---

## 🧩 Available Angular Services

| Service | Description |
|----------|-------------|
| **PostService** | Handles post creation, editing, deletion, and pagination |
| **CommentService** | Manages comments creation and fetching |
| **FileService** | Handles file uploads and downloads |
| **AuthService** | Manages JWT login, signup, and token refresh |
| **FriendService** | Manages friend requests and relationships |

---

## 🧪 Running Tests
```bash
ng test
```
This command launches the Karma test runner for unit tests.

---

## 🧰 Technologies Used
- Angular 17
- Tailwind CSS
- Angular Material
- RxJS
- TypeScript
- Lucide Icons

---

## 🧑‍💻 Developer Notes
- Use `ChangeDetectorRef.markForCheck()` when updating UI after async operations.  
- Always handle API errors gracefully with user-friendly alerts.  
- Store tokens securely in localStorage or sessionStorage.

---

## 📄 License
This project is licensed under the **MIT License**.

---

© 2025 FawryBook Team. All rights reserved.
