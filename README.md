# CollageManagementSystem
🎓 College Management System
A full-stack web-based College Management System developed using Spring Boot, Thymeleaf, Hibernate, and MySQL.
The system helps colleges manage students, faculty, departments, courses, attendance, exams, results, and fees through a centralized platform with role-based access.
---
📌 Project Overview
The College Management System is designed to automate and simplify college administration tasks.
The application provides separate modules for:
Admin
Faculty
Students
The project follows the MVC (Model-View-Controller) architecture and uses Spring Boot with Hibernate/JPA for backend development and MySQL for database management.
---
🚀 Features
👨‍💼 Admin Module
Admin Login
Dashboard Analytics
Manage Students
Manage Faculty
Manage Departments
Manage Courses
Manage Subjects
Manage Attendance
Manage Results
Manage Fees
Role-Based Access Control
---
👨‍🏫 Faculty Module
Faculty Login
View Assigned Subjects
Mark Student Attendance
Manage Exams
Upload Student Results
View Student Information
---
👨‍🎓 Student Module
Student Login
View Attendance
View Results
View Subjects
View Fees Status
Dashboard Access
---
🛠️ Technologies Used
Technology	Purpose
Java 17	Programming Language
Spring Boot 3	Backend Framework
Spring MVC	MVC Architecture
Spring Data JPA	Database Operations
Hibernate	ORM Framework
Thymeleaf	Frontend Template Engine
MySQL	Database
Maven	Dependency Management
HTML/CSS/Bootstrap	Frontend UI
---
🏗️ Project Architecture
The project follows MVC Architecture.
```text
Client Request
      ↓
Controller Layer
      ↓
Service Layer
      ↓
Repository Layer
      ↓
MySQL Database
```
---
▶️ How to Run the Project
Step 1 – Clone the Repository
```bash
git clone https://github.com/your-username/college-management-system.git
```
Step 2 – Create Database
```sql
CREATE DATABASE college_db;
```
Step 3 – Configure Database Credentials
Update application.properties with your MySQL credentials.
Step 4 – Run the Application
```bash
mvn spring-boot:run
```
---
🌐 Application URL
```text
http://localhost:8082
```
---
👤 Default Login Credentials
```text
Email: admin@college.com
Password: admin123
```
---
📚 Learning Outcomes
Spring Boot Development
MVC Architecture
Hibernate & JPA
Database Connectivity
CRUD Operations
Role-Based Authentication
---
👨‍💻 Developed By
Student Name: Devansh Agrawal AND Dhruvesh Biraris
Course: Enterprise Application Development
Academic Year: 2025-26
