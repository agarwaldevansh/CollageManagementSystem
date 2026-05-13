-- ============================================================
-- COLLEGE MANAGEMENT SYSTEM — UPGRADED SCHEMA
-- Tables are auto-created by Hibernate (ddl-auto=update)
-- This file is for reference / manual setup only.
-- ============================================================

CREATE DATABASE IF NOT EXISTS college_db;
USE college_db;

CREATE TABLE IF NOT EXISTS users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     ENUM('ADMIN','STUDENT','FACULTY') NOT NULL
);

CREATE TABLE IF NOT EXISTS person (
    person_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL UNIQUE,
    phone     VARCHAR(20),
    gender    VARCHAR(10),
    dob       DATE         -- stored during signup for records; not used for login
);

CREATE TABLE IF NOT EXISTS department (
    department_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(255) NOT NULL UNIQUE,
    hod             VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS course (
    course_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name   VARCHAR(255) NOT NULL,
    duration      VARCHAR(50),
    department_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE IF NOT EXISTS faculty (
    faculty_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id     BIGINT UNIQUE,
    user_id       BIGINT UNIQUE,
    employee_id   VARCHAR(100) UNIQUE,   -- stored during signup; not used for login
    qualification VARCHAR(255),
    experience    VARCHAR(100),
    department_id BIGINT,
    FOREIGN KEY (person_id)     REFERENCES person(person_id),
    FOREIGN KEY (user_id)       REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE IF NOT EXISTS student (
    student_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id           BIGINT UNIQUE,
    user_id             BIGINT UNIQUE,
    registration_number VARCHAR(100) UNIQUE,  -- stored during signup; not used for login
    admission_year      INT,
    address             VARCHAR(500),
    department_id       BIGINT,
    course_id           BIGINT,
    FOREIGN KEY (person_id)     REFERENCES person(person_id),
    FOREIGN KEY (user_id)       REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES department(department_id),
    FOREIGN KEY (course_id)     REFERENCES course(course_id)
);

CREATE TABLE IF NOT EXISTS subject (
    subject_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(255) NOT NULL,
    subject_type VARCHAR(20) NOT NULL DEFAULT 'COMPULSORY',  -- COMPULSORY | ELECTIVE
    course_id    BIGINT,
    faculty_id   BIGINT,
    FOREIGN KEY (course_id)  REFERENCES course(course_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id)
);

-- ManyToMany join table: student ↔ subject
CREATE TABLE IF NOT EXISTS student_subject (
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, subject_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
);

-- Composite PK: student + subject + date = unique attendance entry
CREATE TABLE IF NOT EXISTS attendance (
    student_id BIGINT   NOT NULL,
    subject_id BIGINT   NOT NULL,
    date       DATE     NOT NULL,
    status     VARCHAR(10) NOT NULL,   -- PRESENT | ABSENT
    remarks    VARCHAR(255),
    PRIMARY KEY (student_id, subject_id, date),
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
);

CREATE TABLE IF NOT EXISTS exam (
    exam_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_type  VARCHAR(50) NOT NULL,
    exam_date  DATE,
    subject_id BIGINT,
    FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
);

CREATE TABLE IF NOT EXISTS result (
    result_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    marks      DOUBLE,
    grade      VARCHAR(5),
    student_id BIGINT,
    exam_id    BIGINT,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (exam_id)    REFERENCES exam(exam_id)
);

CREATE TABLE IF NOT EXISTS fees (
    fee_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount         DOUBLE NOT NULL,
    payment_date   DATE,
    payment_status VARCHAR(20) NOT NULL,
    student_id     BIGINT,
    FOREIGN KEY (student_id) REFERENCES student(student_id)
);

-- Default admin account
INSERT IGNORE INTO users (email, password, role)
VALUES ('admin@college.com', 'admin123', 'ADMIN');

-- ── MIGRATION: Add subject_type column if upgrading from older version ──
-- Run this if you have existing data and get "Unknown column 'subject_type'" error:
-- ALTER TABLE subject ADD COLUMN IF NOT EXISTS subject_type VARCHAR(20) NOT NULL DEFAULT 'COMPULSORY';

-- ── MIGRATION: Add employee_id to faculty ──
-- ALTER TABLE faculty ADD COLUMN IF NOT EXISTS employee_id VARCHAR(100) UNIQUE;

-- ── MIGRATION: Add registration_number to student ──
-- ALTER TABLE student ADD COLUMN IF NOT EXISTS registration_number VARCHAR(100) UNIQUE;

-- ── MIGRATION: Add user_id FK to student and faculty ──
-- ALTER TABLE student ADD COLUMN IF NOT EXISTS user_id BIGINT UNIQUE;
-- ALTER TABLE student ADD CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id);
-- ALTER TABLE faculty ADD COLUMN IF NOT EXISTS user_id BIGINT UNIQUE;
-- ALTER TABLE faculty ADD CONSTRAINT fk_faculty_user FOREIGN KEY (user_id) REFERENCES users(id);

-- ── student_subject join table ──
CREATE TABLE IF NOT EXISTS student_subject (
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, subject_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (subject_id) REFERENCES subject(subject_id)
);
