package com.college.service;

import com.college.model.Subject;
import com.college.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired private SubjectRepository subjectRepository;

    /**
     * FIX: Populate studentCount via DB query on every subject.
     * This prevents LazyInitializationException when Thymeleaf accesses s.studentCount.
     */
    private void enrichWithStudentCount(List<Subject> subjects) {
        for (Subject s : subjects) {
            long count = subjectRepository.countEnrolledStudents(s.getSubjectId());
            s.setStudentCount((int) count);
        }
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        enrichWithStudentCount(subjects);
        return subjects;
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> getSubjectsByCourse(Long courseId) {
        List<Subject> subjects = subjectRepository.findByCourseCourseId(courseId);
        enrichWithStudentCount(subjects);
        return subjects;
    }

    public List<Subject> getSubjectsByFaculty(Long facultyId) {
        List<Subject> subjects = subjectRepository.findByFacultyFacultyId(facultyId);
        enrichWithStudentCount(subjects);
        return subjects;
    }

    public Subject saveSubject(Subject subject) {
        // FIX: default type
        if (subject.getSubjectType() == null || subject.getSubjectType().isBlank()) {
            subject.setSubjectType("COMPULSORY");
        }
        // FIX: validate course is set
        if (subject.getCourse() == null) {
            throw new RuntimeException("Course must be selected for a subject.");
        }
        // FIX: prevent duplicate subject name within same course
        Optional<Subject> existing = subjectRepository.findBySubjectNameAndCourseCourseId(
                subject.getSubjectName(), subject.getCourse().getCourseId());
        if (existing.isPresent()) {
            throw new RuntimeException("Subject '" + subject.getSubjectName()
                    + "' already exists in this course.");
        }
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long id, Subject updated) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // FIX: duplicate check excluding self
        if (updated.getCourse() != null && !updated.getSubjectName().equals(subject.getSubjectName())) {
            Optional<Subject> dup = subjectRepository.findBySubjectNameAndCourseCourseId(
                    updated.getSubjectName(), updated.getCourse().getCourseId());
            if (dup.isPresent() && !dup.get().getSubjectId().equals(id)) {
                throw new RuntimeException("Subject '" + updated.getSubjectName()
                        + "' already exists in this course.");
            }
        }

        subject.setSubjectName(updated.getSubjectName());
        subject.setSubjectType(updated.getSubjectType() != null ? updated.getSubjectType() : "COMPULSORY");
        subject.setCourse(updated.getCourse());
        subject.setFaculty(updated.getFaculty());
        return subjectRepository.save(subject);
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    public long getTotalSubjects() {
        return subjectRepository.count();
    }
}
