package com.college.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class AttendanceId implements Serializable {

    private Long studentId;
    private Long subjectId;
    private LocalDate date;

    public AttendanceId() {}

    public AttendanceId(Long studentId, Long subjectId, LocalDate date) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.date = date;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttendanceId)) return false;
        AttendanceId that = (AttendanceId) o;
        return Objects.equals(studentId, that.studentId) &&
               Objects.equals(subjectId, that.subjectId) &&
               Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, subjectId, date);
    }
}
