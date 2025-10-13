# 📚 Cahier de Textes — UPF Management

A web application designed to **digitize and centralize the management of lesson logs (cahiers de textes)** for the Private University of Fes (Université Privée de Fès - UPF).
This project was developed as part of an end-of-semester academic project and the capstone project for the **ALX Software Engineering — Backend Specialization**. The platform modernizes the manual, paper-based process of managing lesson logs, offering a secure, role-based, and intuitive solution for professors, the student affairs department (Service de Scolarité), the pedagogical service department (Service Pédagogique), and administrators.

---

## 🎯 Summary

This application replaces manual, paper-based lesson logs with a centralized, digital solution tailored for UPF. It streamlines the creation, editing, viewing, and archiving of lesson logs (cahiers de textes) while ensuring secure access, data integrity, and real-time collaboration.

### Key Objectives
- Complete Digitalization: Eliminate paper-based processes for lesson log management.
- Centralized Access: Provide a single platform for all stakeholders to access and manage lesson logs in real-time.
- Ease of Use: Deliver an intuitive, modern interface for creating and editing lesson logs with minimal learning curve.
- Collaboration: Facilitate seamless communication and collaboration among professors, student affairs, pedagogical services, and administrators.
- Security & Traceability: Ensure data integrity with robust security measures and track all changes for accountability.

---

## ✅ Key Features

The application supports role-based access to ensure users only interact with relevant functionalities. Below is a detailed breakdown by role and global features:

### Global Features (All Users)
- Authentication:
  - Secure login with email and password;
  - session-based using Spring Security.
- Profile Management:
  - View and edit personal information;
  - change password (with secure hashing via bcrypt).
- Error Handling:
  - Custom error pages (e.g., 403 Forbidden, 404 Not Found, 500 Internal Server Error) with centralized logging.

### Admin Role
- User Management:
  - Create, edit, view, and delete user accounts (including roles like Professor, Service de Scolarité, Service Pédagogique).
- Base Data Management:
  - Specializations (Filières):
    - Create, edit, delete, and view (including levels and semesters).
  - Modules:
    - Create, edit, delete, and view (independent of specializations, levels, semesters, or professors).
  - Professors:
    - Create, edit, delete, and view.
  - Dashboard & Statistics:
    - Overview of user counts, role distributions, activity logs, and system metrics.

### Service de Scolarité (Student Affairs Department)
- Assignment Management:
  - Assign modules to specializations;
  - Assign modules to professors;
  - edit, delete, and view assignments.
- Lesson Logs Management:
  - View current and archived lesson logs and entries.
  - Delete archived logs.

### Service Pédagogique (Pedagogical Service)
- View-Only Access:
  - View assignments, lesson logs, entries, and archived logs.
  - Used for monitoring course progress and supporting pedagogical decisions.

### Professor Role
- Lesson Logs Management:
  - Create, edit, and view entries in their assigned lesson logs.
- Assignments View:
  - View personal module assignments.

### Additional System-Wide Features:
- Archiving: Securely archive completed lesson logs for historical reference.
- Search and Filtering: Intuitive interfaces for quick data retrieval (e.g., lists of users, modules, specializations).

---

## 🧰 Tech Stack

| Component | Details |
|-----------|---------|
| **Backend Language** | Java 17 (with Eclipse Temurin JDK for runtime) |
| **Backend Framework** | Spring Boot (for rapid development, embedded Tomcat 10.1 server) |
| **Security** | Spring Security (session-based authentication, role-based authorization, input validation) | 
| **ORM & Database Access** | Hibernate / JPA for object-relational mapping | 
| **Database** | MySQL 8.0 (relational DB with schemas for users, filières, modules, affectations, and cahiers de textes) | 
| **Frontend Rendering** | Thymeleaf (server-side templating for dynamic HTML views)
| **Frontend Technologies** | HTML5, CSS (Tailwind CSS for responsive, utility-first styling), JavaScript (with jQuery for enhancements like form validation and modals) | 
| **Build & Dependency Management** | Maven (for building and managing dependencies; use mvn or ./mvnw) |

---

## 🏗 Project Structure
The project follows a standard Spring Boot layout with modular organization:

- src/main/java/com/upf/management:
  - Config: Security configurations (e.g., Spring Security setup).
  - Controller: RESTful and view controllers for handling requests (e.g., UserController, FiliereController).
  - Entity: JPA entities (e.g., User, Filiere, Module, Affectation, CahierDeTextes).
  - Repository: JPA repositories for data access (e.g., UserRepository).
  - Service: Business logic layers (e.g., UserService for CRUD operations).
- src/main/resources:
  - templates: Thymeleaf HTML files (e.g., login.html, dashboard.html, filiere/list.html).
  - static: Assets like CSS (tailwind.css), JS (scripts.js), images (logos, icons).
  - application.properties: Configuration for DB connection, server port (3000), logging, etc.
- database/: SQL scripts for schema creation and data seeding (e.g., inserts.sql for initial data; passwords.json for sample user credentials).
- Dockerfile: Multi-stage build for compiling with Maven and running the JAR.
- docker-compose.yaml: Orchestrates MySQL DB and Spring app containers; auto-populates DB on startup via a seeder container.
- entrypoint.sh: Bash script to wait for MySQL readiness before starting the app (uses nc for polling).
- pom.xml: Maven dependencies (Spring Boot starters, Thymeleaf, MySQL connector, etc.).

---

## 🚀 Setup & Installation
#### Local Development (Without Docker)
- Prerequisites: Java 17, Maven, MySQL 8.0, Git.
- Clone the repo:
```bash
git clone https://github.com/Assiminee/cahier_de_textes.git
cd cahier_de_textes
```
- Set up MySQL:
  - Create database cahier_de_textes;
  - run the script database/inserts.sql to initialize schema and data.
- Configure application.properties with DB credentials.
- Build & run:
```bash
mvn clean install
mvn spring-boot:run
```
Access: http://localhost:3000 (login with credentials from **database/passwords.json**).
#### Docker Setup (Recommended for Easy Testing)
The entire application is containerized for quick setup without local dependencies. Docker Compose handles the Spring app, MySQL DB, and automatic DB seeding.
- Prerequisites: Docker and Docker Compose installed.
- Clone the repo and navigate to the root.
- Run:
```bash
docker compose up -d
```
- DB Container: Uses MySQL 8.0; auto-creates cahier_de_textes DB; health-checked for readiness.
- App Container: Builds from Dockerfile; waits for DB via entrypoint.sh; runs on port 3000.
- Seeder Container: Runs once to populate DB with initial data (from database/inserts.sql).
- Access: http://localhost:3000.
- Sample Credentials: Find emails, passwords, and roles in **database/passwords.json** (includes users for all roles: Admin, Professor, Service de Scolarité, Service Pédagogique).
- Stop:
```bash
docker compose down
```
Note: The Docker setup ensures the DB is populated on first run, allowing immediate testing across roles without manual setup.

---

## 📈 Usage & Navigation
For a step-by-step visual guide, watch the video tutorial below:

<a href="https://vimeo.com/1126653083" target="_blank"><img src="https://github.com/Assiminee/cahier_de_textes/blob/main/thumbnail.png" alt="Video Thumbnail"></a>
