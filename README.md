🏥 Hospital Management System
A Comprehensive Enterprise-Grade Healthcare Management Platform
📋 Project Overview
The Hospital Management System is a robust, scalable, and secure backend solution built with Spring Boot that streamlines healthcare operations. This enterprise-grade application provides comprehensive management of patients, doctors, appointments, and administrative functions with advanced features like JWT authentication, role-based access control, and intelligent caching mechanisms.

✨ Key Features
🔐 Security & Authentication
JWT-based Authentication with secure token management

Role-Based Access Control (RBAC) with three distinct roles:

ADMIN - Full system access and management

DOCTOR - Appointment management and patient viewing

PATIENT - Self-service appointment booking

OAuth2 Client Support for social login integration

Password Encryption using BCrypt

Custom UserDetailsService for flexible user management

👨‍⚕️ Doctor Management
Complete CRUD operations for doctors

Onboarding workflow for new doctors

Specialization-based categorization

Department association capabilities

Email-based unique identification

👤 Patient Management
Comprehensive patient profiles

Medical history tracking

Blood group classification

Insurance management integration

Unique constraints on name and birth date

📅 Appointment System
Intelligent Scheduling with conflict prevention

Doctor Availability Management

Patient Appointment History

Appointment Re-assignment across doctors

Time-based appointment tracking

Reason and Notes for each appointment

🚀 Performance Optimization
In-Memory Caching with Caffeine

Intelligent Cache Eviction Strategies

Cacheable Operations for frequently accessed data

TTL-based Cache Expiration (10 minutes default)

Custom Cache Keys for optimal retrieval

📊 Data Management
PostgreSQL database integration

Hibernate ORM for seamless object-relational mapping

Automatic Table Generation with DDL auto

SQL Initialization scripts for test data

ModelMapper for efficient DTO conversions

🛡️ Security Features
Method-level Security with @PreAuthorize and @Secured

JWT Filter Chain for request validation

Provider-based Authentication (Email, Google, GitHub, Facebook)

Session Management with Spring Security

🏗️ Architecture
Technology Stack
Component	Technology	Version
Framework	Spring Boot	3.5.3
ORM	Hibernate / JPA	6.6.18
Database	PostgreSQL	18.3
Security	Spring Security + JWT	6.5.1
Caching	Caffeine	3.2.1
Mapping	ModelMapper	3.2.0
Build	Maven	-
Java	Java	24

📦 Database Schema
Core Entities
Entity	Description	Key Fields
AppUser	Authentication and authorization	id, username, password, provider_type
Patient	Patient profiles	user_id, name, email, blood_group
Doctor	Medical professionals	user_id, name, specialization, email
Appointment	Medical appointments	id, appointment_time, reason, doctor, patient
Department	Medical departments	id, name, head_doctor
Insurance	Patient insurance details	id, provider, policy_number
Relationships
Patient ↔ Appointment: One-to-Many

Doctor ↔ Appointment: One-to-Many

Patient ↔ User: One-to-One

Doctor ↔ User: One-to-One

Doctor ↔ Department: Many-to-Many

🚀 Getting Started
Prerequisites
bash
- Java 24 or higher
- Maven 3.8+
- PostgreSQL 18.3+
- Git
Installation
Clone the repository

bash
git clone https://github.com/yourusername/hospital-management-system.git
cd hospital-management-system
Configure Database

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/HospitalDB
spring.datasource.username=your_username
spring.datasource.password=your_password
Build and Run

bash
mvn clean install
mvn spring-boot:run
Access the Application

text
Base URL: http://localhost:8080/api/v1
📊 API Endpoints

Authentication
text
POST   /auth/signup       - Register new user
POST   /auth/login        - Login and get JWT token


Patients
text
POST   /patients/appointments  - Book appointment
GET    /patients/profile       - Get patient profile
GET    /patients/appointments/{id} - Get appointment details


Doctors
text
GET    /doctors/allDoctors     - Get all doctors
GET    /doctors/appointments   - Get doctor's appointments
