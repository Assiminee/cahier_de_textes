# 📚 Cahier de Textes — UPF Management

A web application to **digitize and centralize the management of cahiers de textes** (lesson logs) for the Université Privée de Fès (UPF).  
This project was developed as an end-of-semester academic project and as the **capstone / final project** for the **ALX Software Engineering — Backend Specialization**.

---

## 🎯 Summary

Cahier de Textes replaces paper-based lesson logs with a secure, role-based web platform where professors, the Service de Scolarité and the Service Pédagogique can create, view, edit and archive lesson entries. The system emphasizes accessibility, traceability, and data integrity.

---

## ✅ Key Features

- Role-based authentication and authorization (Admin, Professeur, Service de Scolarité, Service Pédagogique)
- Create / Edit / View / Archive **Cahiers de Textes** (lesson entries)
- Management of **Filières**, **Modules**, **Professeurs**, **Affectations**
- Profiles and password management (change / reset)
- Admin dashboard & statistics (user counts, distributions, etc.)
- Error pages and centralized error handling
- Security measures: Spring Security, input validation, CSRF protection recommendations

---

## 🧰 Tech Stack

- **Backend language:** Java 17  
- **Backend framework:** Spring Boot  
- **Security:** Spring Security (session-based auth; custom login endpoints)  
- **ORM:** Hibernate / JPA  
- **Template engine / Frontend rendering:** Thymeleaf (server-side rendered views)  
- **Frontend technologies:** HTML, CSS, Tailwind CSS, JavaScript, jQuery (progressive enhancements)  
- **Database:** MySQL (production-grade relational DB)  
- **Application Server:** Tomcat 10.1 (embedded via Spring Boot or external)  
- **Build & Dependency Management:** Maven (mvn / mvnw)  
- **Dev IDE:** IntelliJ IDEA (recommended)  
- **Version control:** Git / GitHub

## 🏗 Project Structure

- `src/main/java` — Spring Boot application, controllers, services, repositories, entities
- `src/main/resources/templates` — Thymeleaf HTML templates
- `src/main/resources/static` — CSS (Tailwind), JS, images
- `src/main/resources/application.properties` — DB and app config
- `database/` — scripts to populate the database
