package com.college.repository;

import com.college.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findBySubjectSubjectId(Long subjectId);
    List<Exam> findBySubjectFacultyFacultyId(Long facultyId);
    long countBySubjectFacultyFacultyId(Long facultyId);
}
