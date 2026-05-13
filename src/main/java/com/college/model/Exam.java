package com.college.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "exam_type", nullable = false)
    private String examType;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // FIX: was List<Result> — broken type
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Result> results = new ArrayList<>();

    public Exam() {}
    public Long getExamId() { return examId; }
    public void setExamId(Long id) { this.examId = id; }
    public String getExamType() { return examType; }
    public void setExamType(String t) { this.examType = t; }
    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate d) { this.examDate = d; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject s) { this.subject = s; }
    public List<Result> getResults() { return results; }
    public void setResults(List<Result> r) { this.results = r; }
}
