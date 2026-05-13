package com.college.repository;

import com.college.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByCourseCourseId(Long courseId);
    List<Subject> findByFacultyFacultyId(Long facultyId);
    long countByFacultyFacultyId(Long facultyId);

    // Compulsory subjects for a course (auto-enroll on student registration)
    List<Subject> findByCourseCourseIdAndSubjectType(Long courseId, String subjectType);

    // FIX: Duplicate name check within same course
    Optional<Subject> findBySubjectNameAndCourseCourseId(String subjectName, Long courseId);

    // DB-backed student count — no lazy collection access
    @Query("SELECT COUNT(st) FROM Student st JOIN st.enrolledSubjects s WHERE s.subjectId = :subjectId")
    long countEnrolledStudents(@Param("subjectId") Long subjectId);

    // Electives not yet enrolled by this student
    @Query("SELECT s FROM Subject s WHERE s.course.courseId = :courseId " +
           "AND s.subjectType = 'ELECTIVE' " +
           "AND s.subjectId NOT IN " +
           "(SELECT sub.subjectId FROM Student st JOIN st.enrolledSubjects sub WHERE st.studentId = :studentId)")
    List<Subject> findAvailableElectivesForStudent(@Param("courseId") Long courseId,
                                                    @Param("studentId") Long studentId);

    // All unenrolled subjects (any type)
    @Query("SELECT s FROM Subject s WHERE s.course.courseId = :courseId " +
           "AND s.subjectId NOT IN " +
           "(SELECT sub.subjectId FROM Student st JOIN st.enrolledSubjects sub WHERE st.studentId = :studentId)")
    List<Subject> findAvailableSubjectsForStudent(@Param("courseId") Long courseId,
                                                   @Param("studentId") Long studentId);
}
