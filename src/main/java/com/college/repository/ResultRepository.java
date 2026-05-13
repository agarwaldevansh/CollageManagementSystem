package com.college.repository;

import com.college.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    
    List<Result> findByStudentStudentId(Long studentId);
    List<Result> findByExamExamId(Long examId);
}
