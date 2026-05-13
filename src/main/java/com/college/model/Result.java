package com.college.model;

import jakarta.persistence.*;

@Entity
@Table(name = "result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    private Double marks;
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    public Result() {}
    public Long getResultId() { return resultId; }
    public void setResultId(Long id) { this.resultId = id; }
    public Double getMarks() { return marks; }
    public void setMarks(Double m) { this.marks = m; }
    public String getGrade() { return grade; }
    public void setGrade(String g) { this.grade = g; }
    public Student getStudent() { return student; }
    public void setStudent(Student s) { this.student = s; }
    public Exam getExam() { return exam; }
    public void setExam(Exam e) { this.exam = e; }
}
