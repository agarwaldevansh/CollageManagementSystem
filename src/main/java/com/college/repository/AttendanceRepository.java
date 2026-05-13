package com.college.repository;

import com.college.model.Attendance;
import com.college.model.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {
    List<Attendance> findByStudentStudentId(Long studentId);
    List<Attendance> findBySubjectSubjectId(Long subjectId);

    // Check for existing attendance (prevents duplicates)
    Optional<Attendance> findByStudentStudentIdAndSubjectSubjectIdAndIdDate(
            Long studentId, Long subjectId, LocalDate date);

    // All attendance for a subject on a specific date (for bulk view)
    @Query("SELECT a FROM Attendance a WHERE a.subject.subjectId = :subjectId AND a.id.date = :date")
    List<Attendance> findBySubjectAndDate(@Param("subjectId") Long subjectId,
                                          @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.studentId = :sid AND a.status = 'PRESENT'")
    long countPresentByStudentId(@Param("sid") Long studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.studentId = :sid")
    long countTotalByStudentId(@Param("sid") Long studentId);

    // Per-subject attendance summary for a student
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.studentId = :sid " +
           "AND a.subject.subjectId = :subId AND a.status = 'PRESENT'")
    long countPresentByStudentAndSubject(@Param("sid") Long studentId, @Param("subId") Long subjectId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.studentId = :sid " +
           "AND a.subject.subjectId = :subId")
    long countTotalByStudentAndSubject(@Param("sid") Long studentId, @Param("subId") Long subjectId);
}
