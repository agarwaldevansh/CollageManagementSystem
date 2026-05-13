package com.college.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Link User → Student (nullable for ADMIN/FACULTY users)
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Student student;

    // Link User → Faculty (nullable for ADMIN/STUDENT users)
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Faculty faculty;

    public enum Role {
        ADMIN, STUDENT, FACULTY
    }

    public User() {}
    public User(String email, String password, Role role) {
        this.email = email; this.password = password; this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
}
