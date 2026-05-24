# Salesforce Validation Rule Manager (Full Stack Project)

## 📌 Project Overview

This is a full-stack web application integrated with Salesforce using OAuth 2.0. The system allows users to log in to Salesforce, fetch validation rules from a Salesforce Developer Org, view their status, and toggle (activate/deactivate) them directly from the UI.

The project demonstrates Salesforce OAuth integration, REST API communication, and full-stack application development using React and Spring Boot.

The project consists of:

- Frontend: React.js
- Backend: Spring Boot (Java)
- Integration: Salesforce OAuth 2.0 + Tooling API

---

# 🛠 Tech Stack

## Frontend
- React JS
- Axios
- HTML
- CSS
- JavaScript

## Backend
- Spring Boot
- Spring Web MVC
- RestTemplate
- Salesforce Tooling API
- OAuth 2.0 (PKCE Flow)

---

# 🚀 Features

## 🔐 Salesforce Login
- User clicks **Login to Salesforce**
- Redirects to Salesforce official login page
- After authentication, redirects back to application

## 📋 Fetch Validation Rules
- Retrieves validation rules from Salesforce org
- Displays:
  - Rule Name
  - Status (Active / Inactive)

## 🔄 Toggle Validation Rules
- Activate or deactivate validation rules
- Toggle functionality updates the validation rule status in the application UI

---

# 🌐 Live Deployment

## Frontend
https://salesforce-frontend-41ny.onrender.com

## Backend
https://salesforce-validation-manager-snah.onrender.com

---

# 📂 Project Structure

## Frontend

```text
src/
│── App.js
│── App.css
│── index.js
```

## Backend

```text
src/main/java/com/cloudvandana/backend/
│
├── controller/
│   └── AuthController.java
│
├── service/
│   └── SalesforceService.java
│
├── dto/
│   ├── TokenResponse.java
│   └── ValidationRuleResponse.java
│
├── config/
│   ├── AppConfig.java
│   └── SecurityConfig.java
│
└── BackendvandanaApplication.java
```

---

# ⚙️ Backend API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/login` | Redirects to Salesforce OAuth login |
| GET | `/api/callback` | Handles OAuth redirect and token exchange |
| GET | `/api/validation-rules` | Fetches all validation rules |
| GET | `/api/toggle-rule` | Toggles validation rule status in UI |

---

# ▶️ How to Run Locally

## 1. Clone Repository

```bash
git clone <your-repo-link>
```

---

## 2. Backend Setup (Spring Boot)

```bash
cd backendvandana
```

Configure `application.properties`

```properties
server.port=8080

salesforce.client.id=YOUR_CLIENT_ID
salesforce.client.secret=YOUR_CLIENT_SECRET

salesforce.redirect.uri=http://localhost:8080/api/callback
salesforce.auth.url=https://login.salesforce.com/services/oauth2/authorize
salesforce.token.url=https://login.salesforce.com/services/oauth2/token
```

Run Backend

```bash
mvn spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

---

## 3. Frontend Setup (React)

```bash
cd salesforce-frontend
npm install
npm start
```

Frontend runs at:

```text
http://localhost:3000
```

---

# 🔐 Salesforce Setup

1. Create Salesforce Developer Org
2. Create Connected App
3. Enable OAuth Settings
4. Add Redirect URI:

```text
http://localhost:8080/api/callback  (local testing)
`https://salesforce-validation-manager-snah.onrender.com/api/callback   (render testing)
``
The callback URL in:
Salesforce Connected App & backend application.properties must match exactly.

5. Enable scopes:
- Access and manage your data (API)
- Perform requests on your behalf at any time

---

# 🔄 OAuth Flow

```text
Frontend calls /api/login
        ↓
Backend redirects to Salesforce login page
        ↓
User authenticates
        ↓
Salesforce redirects to /api/callback
        ↓
Backend receives authorization code
        ↓
Access token is generated and stored
        ↓
Frontend can now call secured APIs
```

---

# 📌 Notes

- Backend must run before frontend
- Salesforce connected app must be configured correctly
- OAuth flow is required before accessing rules
- Without login, validation rules API will fail
- ***If already logged into Salesforce, OAuth page may auto-skip login screen***

---

# 👨‍💻 Author

**Navya Sri Narsingoju**  
(Preethika Narsingoju)

CloudVandana Associate Software Engineer Assignment Project

---

# ✅ Final Status

✔ Salesforce OAuth Login  
✔ Fetch Validation Rules  
✔ Toggle Validation Rules  
✔ Full Stack Integration Completed  
✔ Working End-to-End Flow