# Fitness Revolution — Gym Management System

A modern, lightweight, and comprehensive **Gym Management System** built with **Java Spring Boot** (backend) and **Vanilla HTML, CSS, and JavaScript** (frontend). The project utilizes a flat-file database system (CSV) for simplicity, portability, and easy review.

---

## 🚀 Key Modules & Features

### 1. 👥 Member Management
* Register new members with personal details, addresses, and selected membership types.
* Search and filter members by ID, name, or email.
* Monitor membership status (Active, Expired, Expiring Soon, Inactive).
* Renew memberships and toggle active status.

### 2. 📅 Attendance Tracking
* Real-time Check-In and Check-Out system for registered members.
* Quick-action dashboard displaying today's active members in the gym.
* Detailed daily attendance logs and history with search/filter capabilities.
* Real-time attendance stats (Total today, Currently present, Completed sessions).

### 3. 🏋️ Trainer Management
* Manage fitness trainers, their profiles, specializations, and availability.
* Schedule and track training sessions between trainers and members.

### 4. 💳 Membership Types
* Define various membership plans (e.g., Monthly, Yearly, Student, Family).
* Customize membership periods, pricing, and benefits.

---

## 📁 Project Directory Structure

```text
fitness-revolution/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fitnessRevolution/
│   │   │       ├── controller/             # REST Controllers (API Endpoints)
│   │   │       │   ├── AttendanceController.java
│   │   │       │   ├── MemberController.java
│   │   │       │   ├── MembershipTypeController.java
│   │   │       │   └── TrainerController.java
│   │   │       ├── model/                  # Data Entity Models
│   │   │       │   ├── AttendanceRecord.java
│   │   │       │   ├── Member.java
│   │   │       │   ├── MembershipType.java
│   │   │       │   ├── Trainer.java
│   │   │       │   └── TrainingSession.java
│   │   │       ├── service/                # Business Logic Services
│   │   │       │   ├── AttendanceService.java
│   │   │       │   ├── MemberService.java
│   │   │       │   ├── MembershipTypeService.java
│   │   │       │   └── TrainerService.java
│   │   │       ├── storage/                # Storage Helpers
│   │   │       │   └── FileStorageUtil.java
│   │   │       └── FitnessRevolutionApplication.java  # Main Class
│   │   └── resources/
│   │       ├── data/                       # CSV Flat File Data Storage
│   │       │   ├── attendance.txt
│   │       │   ├── members.txt
│   │       │   ├── membership_types.txt
│   │       │   └── trainers.txt
│   │       ├── static/                     # Frontend Web Pages & Scripts
│   │       │   ├── css/
│   │       │   │   └── style.css           # Premium Dark Mode Styling
│   │       │   ├── js/
│   │       │   │   ├── attendance.js       # Attendance Scripts
│   │       │   │   ├── members.js          # Member Scripts
│   │       │   │   ├── trainers.js         # Trainer Scripts
│   │       │   │   ├── payments.js
│   │       │   │   └── reports.js
│   │       │   ├── admin.html              # Admin Control Panel
│   │       │   ├── attendance.html         # Attendance Management UI
│   │       │   ├── index.html              # Login Landing Page
│   │       │   ├── members.html            # Member Dashboard
│   │       │   ├── memberships.html        # Membership Plans UI
│   │       │   ├── portal.html             # Member Self-Service Portal
│   │       │   └── trainers.html           # Trainer Dashboard
│   │       └── application.properties       # Spring Configurations
└── pom.xml                                 # Maven Dependencies
```

---

## 🛠️ Technology Stack

* **Backend:** Java 17, Spring Boot (Web, DevTools)
* **Frontend:** HTML5, CSS3 (Premium dark theme, HSL customized palettes, responsive grid layouts), ES6 JavaScript (Fetch API)
* **Data Storage:** Flat CSV text files (`src/main/resources/data/`) with custom serialization/deserialization logic.

---

## 💻 Setup & Execution

### Prerequisites
* Java Development Kit (JDK) 17 or higher.
* Maven (included as `mvnw` wrapper).

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone https://github.com/IT25103510/Fitness-Revolution.git
   ```

2. **Navigate to the project root:**
   ```bash
   cd Fitness-Revolution/fitness-revolution
   ```

3. **Run the Spring Boot application:**
   * **Windows:**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   * **Mac / Linux:**
     ```bash
     ./mvnw spring-boot:run
     ```

4. **Access the application:**
   * Open your web browser and navigate to: `http://localhost:8080`

---

## 🔑 Login & Access Credentials

* **Admin Portal:**
  * Click on the **Admin** tab.
  * Enter Password: `admin123`
* **Member Portal:**
  * Click on the **Member** tab.
  * Sign in using any active email in `members.txt` (e.g., `jone123@gmail.com` or `kavishan@gmail.com`).