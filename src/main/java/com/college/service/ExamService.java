package com.college.service;

import com.college.model.Exam;
import com.college.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    public List<Exam> getExamsBySubject(Long subjectId) {
        return examRepository.findBySubjectSubjectId(subjectId);
    }

    public List<Exam> getExamsByFaculty(Long facultyId) {
        return examRepository.findBySubjectFacultyFacultyId(facultyId);
    }

    public Exam saveExam(Exam exam) {
        return examRepository.save(exam);
    }

    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }
}
