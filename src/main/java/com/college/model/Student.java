package com.college.model;

import jakarta.persistence.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // EAGER so profile/dashboard never LazyInitializationException on person.name
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", referencedColumnName = "person_id")
    private Person person;

    @Column(name = "registration_number", unique = true)
    private String registrationNumber;

    @Column(name = "admission_year")
    private Integer admissionYear;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // EAGER: needed on every dashboard/enrollment page — avoids LazyInit crash
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "student_subject",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<Subject> enrolledSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances = new ArrayList<>();

    // FIX: was List<Result> — broken type reference
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Result> results = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Fees> fees = new ArrayList<>();

    public Student() {}

    // Calculated field — year 1-4 from admissionYear
    @Transient
    public Integer getCurrentYear() {
        if (admissionYear == null) return null;
        int diff = Year.now().getValue() - admissionYear + 1;
        return Math.max(1, Math.min(diff, 4));
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String r) { this.registrationNumber = r; }
    public Integer getAdmissionYear() { return admissionYear; }
    public void setAdmissionYear(Integer admissionYear) { this.admissionYear = admissionYear; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public List<Subject> getEnrolledSubjects() { return enrolledSubjects; }
    public void setEnrolledSubjects(List<Subject> s) { this.enrolledSubjects = s; }
    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> a) { this.attendances = a; }
    public List<Result> getResults() { return results; }
    public void setResults(List<Result> r) { this.results = r; }
    public List<Fees> getFees() { return fees; }
    public void setFees(List<Fees> f) { this.fees = f; }
}
