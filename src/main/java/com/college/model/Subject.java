package com.college.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subject",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_subject_name_course",
           columnNames = {"subject_name", "course_id"}
       ))
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "subject_type", nullable = false)
    private String subjectType = "COMPULSORY"; // COMPULSORY | ELECTIVE

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    // EAGER: faculty name shown everywhere — avoids LazyInit in templates
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    // FIX: mappedBy side stays LAZY — never access this in templates directly
    // Use a separate count field (studentCount) populated by service layer
    @ManyToMany(mappedBy = "enrolledSubjects", fetch = FetchType.LAZY)
    private List<Student> enrolledStudents = new ArrayList<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exam> exams = new ArrayList<>();

    // FIX: This is NOT @Transient anymore — it's a non-persisted field set by service
    // This avoids the LazyInitializationException on enrolledStudents.size()
    @Transient
    private int studentCount = 0;

    public Subject() {}

    @Transient
    public boolean isCompulsory() { return "COMPULSORY".equals(subjectType); }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long id) { this.subjectId = id; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String n) { this.subjectName = n; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String t) { this.subjectType = t; }
    public Course getCourse() { return course; }
    public void setCourse(Course c) { this.course = c; }
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty f) { this.faculty = f; }
    public List<Student> getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(List<Student> s) { this.enrolledStudents = s; }
    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> a) { this.attendances = a; }
    public List<Exam> getExams() { return exams; }
    public void setExams(List<Exam> e) { this.exams = e; }
    // studentCount set by service layer, never triggers lazy load
    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }
}
