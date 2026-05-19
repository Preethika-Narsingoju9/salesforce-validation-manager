# Salesforce Validation Rule Manager (Full Stack Project)

## 📌 Project Overview
This is a full-stack web application integrated with Salesforce using OAuth 2.0. The system allows users to log in to Salesforce, fetch validation rules from a Salesforce Developer Org, view their status, and toggle (activate/deactivate) them directly from the UI.

The project consists of:
- Frontend: React.js
- Backend: Spring Boot (Java)
- Integration: Salesforce OAuth 2.0 + Tooling API

---

## 🛠 Tech Stack

### Frontend
- React JS
- Axios
- HTML, CSS, JavaScript

### Backend
- Spring Boot
- Spring Web MVC
- RestTemplate
- Salesforce Tooling API
- OAuth 2.0 (PKCE Flow)

---

## 🚀 Features

### 🔐 Salesforce Login
- User clicks **Login to Salesforce**
- Redirects to Salesforce official login page
- After authentication, redirects back to application

### 📋 Fetch Validation Rules
- Retrieves validation rules from Salesforce org
- Displays:
  - Rule Name
  - Status (Active / Inactive)

### 🔄 Toggle Validation Rules
- Activate or deactivate validation rules
- Changes reflect directly in Salesforce org

---

## 📂 Project Structure

### Frontend

src/
│── App.js
│── App.css
│── index.js


### Backend

src/main/java/com/cloudvandana/backend/
│
├── controller/
│ └── AuthController.java
│
├── service/
│ └── SalesforceService.java
│
├── dto/
│ ├── TokenResponse.java
│ └── ValidationRuleResponse.java
│
├── config/
│ ├── AppConfig.java
│ └── SecurityConfig.java
│
└── BackendvandanaApplication.java

---

## ⚙️ Backend API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/login` | Redirects to Salesforce OAuth login |
| GET | `/callback` | Handles OAuth redirect and token exchange |
| GET | `/validation-rules` | Fetches all validation rules |
| GET | `/toggle-rule` | Toggles rule active/inactive |

---

## ▶️ How to Run Locally

### 1. Clone Repository
git clone <your-repo-link>

### 2. Backend Setup (Spring Boot)
cd backend
Configure application.properties

server.port=8080

salesforce.client.id=YOUR_CLIENT_ID  
salesforce.client.secret=YOUR_CLIENT_SECRET  
salesforce.redirect.uri=http://localhost:8080/callback  
salesforce.auth.url=https://login.salesforce.com/services/oauth2/authorize  
salesforce.token.url=https://login.salesforce.com/services/oauth2/token  

Run Backend
mvn spring-boot:run

Backend runs at:

http://localhost:8080

### 3. Frontend Setup (React)
cd frontend
npm install
npm start

Frontend runs at:

http://localhost:3000

### 🔐 Salesforce Setup
1. Create Salesforce Developer Org
2. Create Connected App
3. Enable OAuth Settings
4. Add Redirect URI:
    http://localhost:8080/callback
5. Enable scopes:
    Access and manage your data (API)
    Perform requests on your behalf

### 🔄 OAuth Flow
    Frontend calls /login
    Backend redirects to Salesforce login page
    User authenticates
    Salesforce redirects to /callback
    Backend receives authorization code
    Access token is generated and stored
    Frontend can now call secured APIs

### 📌 Notes
    Backend must run before frontend
    Salesforce connected app must be configured correctly
    OAuth flow is required before accessing rules
    Without login, validation rules API will fail


👨‍💻 Author
Navya sri Narsingoju
-CloudVandana Associate Software Engineer Assignment Project


✅ Final Status

✔ Salesforce OAuth Login
✔ Fetch Validation Rules
✔ Toggle Validation Rules
✔ Full Stack Integration Completed
✔ Working End-to-End Flow