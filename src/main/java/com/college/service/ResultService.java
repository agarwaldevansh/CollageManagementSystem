package com.college.service;

import com.college.model.Exam;
import com.college.model.Result;
import com.college.model.Student;
import com.college.repository.ExamRepository;
import com.college.repository.ResultRepository;
import com.college.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResultService {

    @Autowired private ResultRepository resultRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ExamRepository examRepository;

    // FIX: all were returning List<Result> — broken type
    public List<Result> getAllResults() { return resultRepository.findAll(); }

    public List<Result> getResultsByStudent(Long studentId) {
        return resultRepository.findByStudentStudentId(studentId);
    }

    public List<Result> getResultsByExam(Long examId) {
        return resultRepository.findByExamExamId(examId);
    }

    public Result saveResult(Long studentId, Long examId, Double marks) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        Result result = new Result();
        result.setStudent(student);
        result.setExam(exam);
        result.setMarks(marks);
        result.setGrade(calcGrade(marks));
        return resultRepository.save(result);
    }

    public void deleteResult(Long id) { resultRepository.deleteById(id); }

    private String calcGrade(Double m) {
        if (m == null) return "N/A";
        if (m >= 90) return "A+"; if (m >= 80) return "A";
        if (m >= 70) return "B";  if (m >= 60) return "C";
        if (m >= 50) return "D";  return "F";
    }
}
