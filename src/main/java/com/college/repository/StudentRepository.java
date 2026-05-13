package com.college.repository;

import com.college.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByPersonEmail(String email);
    Optional<Student> findByRegistrationNumber(String registrationNumber);
    List<Student> findByDepartmentDepartmentId(Long deptId);
    List<Student> findByCourseCourseId(Long courseId);
    long countByDepartmentDepartmentId(Long deptId);

    @Query("SELECT s FROM Student s JOIN s.enrolledSubjects sub WHERE sub.subjectId = :subjectId")
    List<Student> findByEnrolledSubject(@Param("subjectId") Long subjectId);

    @Query("SELECT s.admissionYear, COUNT(s) FROM Student s GROUP BY s.admissionYear ORDER BY s.admissionYear")
    List<Object[]> countByAdmissionYear();
}
