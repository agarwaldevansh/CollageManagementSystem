package com.college.service;

import com.college.model.Student;
import com.college.model.Subject;
import com.college.repository.AttendanceRepository;
import com.college.repository.StudentRepository;
import com.college.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private SubjectRepository subjectRepository;

    public List<Student> getAllStudents() { return studentRepository.findAll(); }
    public Optional<Student> getStudentById(Long id) { return studentRepository.findById(id); }
    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByPersonEmail(email);
    }
    public Student saveStudent(Student student) { return studentRepository.save(student); }
    public void deleteStudent(Long id) { studentRepository.deleteById(id); }
    public long getTotalStudents() { return studentRepository.count(); }
    public List<Object[]> getAdmissionsPerYear() { return studentRepository.countByAdmissionYear(); }

    public double getAttendancePercentage(Long studentId) {
        long total = attendanceRepository.countTotalByStudentId(studentId);
        if (total == 0) return 0.0;
        long present = attendanceRepository.countPresentByStudentId(studentId);
        return Math.round((present * 100.0 / total) * 10.0) / 10.0;
    }

    @Transactional
    public void autoEnrollCompulsorySubjects(Student student) {
        if (student.getCourse() == null) return;
        List<Subject> compulsory = subjectRepository.findByCourseCourseIdAndSubjectType(
                student.getCourse().getCourseId(), "COMPULSORY");
        for (Subject s : compulsory) {
            boolean already = student.getEnrolledSubjects().stream()
                    .anyMatch(e -> e.getSubjectId().equals(s.getSubjectId()));
            if (!already) student.getEnrolledSubjects().add(s);
        }
        studentRepository.save(student);
    }

    @Transactional
    public void enrollInSubject(Long studentId, Long subjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        if (student.getEnrolledSubjects().stream()
                .anyMatch(s -> s.getSubjectId().equals(subjectId))) {
            throw new RuntimeException("Already enrolled in this subject.");
        }
        student.getEnrolledSubjects().add(subject);
        studentRepository.save(student);
    }

    @Transactional
    public void dropSubject(Long studentId, Long subjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        // FIX: Cannot drop compulsory subjects
        if ("COMPULSORY".equals(subject.getSubjectType())) {
            throw new RuntimeException("Cannot drop a compulsory subject.");
        }
        student.getEnrolledSubjects().removeIf(s -> s.getSubjectId().equals(subjectId));
        studentRepository.save(student);
    }

    public List<Student> getStudentsBySubject(Long subjectId) {
        return studentRepository.findByEnrolledSubject(subjectId);
    }

    public Student updateStudent(Long id, Student updated) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.getPerson().setName(updated.getPerson().getName());
        student.getPerson().setPhone(updated.getPerson().getPhone());
        student.getPerson().setGender(updated.getPerson().getGender());
        student.setAddress(updated.getAddress());
        student.setDepartment(updated.getDepartment());
        student.setCourse(updated.getCourse());
        return studentRepository.save(student);
    }
}
