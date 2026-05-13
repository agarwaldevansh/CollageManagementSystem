package com.college.service;

import com.college.model.Attendance;
import com.college.model.AttendanceId;
import com.college.model.Student;
import com.college.model.Subject;
import com.college.repository.AttendanceRepository;
import com.college.repository.StudentRepository;
import com.college.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private SubjectRepository subjectRepository;

    public List<Attendance> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentStudentId(studentId);
    }

    public List<Attendance> getAttendanceBySubject(Long subjectId) {
        return attendanceRepository.findBySubjectSubjectId(subjectId);
    }

    public List<Attendance> getAttendanceBySubjectAndDate(Long subjectId, LocalDate date) {
        return attendanceRepository.findBySubjectAndDate(subjectId, date);
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    // Single mark with upsert
    @Transactional
    public Attendance markAttendance(Long studentId, Long subjectId,
                                     String status, String remarks, LocalDate date) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        LocalDate d = (date != null) ? date : LocalDate.now();
        AttendanceId id = new AttendanceId(studentId, subjectId, d);

        Attendance attendance = attendanceRepository.findById(id).orElse(new Attendance());
        attendance.setId(id);
        attendance.setStudent(student);
        attendance.setSubject(subject);
        attendance.setStatus(status);
        attendance.setRemarks(remarks != null ? remarks : "");
        return attendanceRepository.save(attendance);
    }


    @Transactional(rollbackFor = Exception.class)
    public int saveAttendanceBatch(Long subjectId, LocalDate date,
                                   Map<Long, String> statuses,
                                   Map<Long, String> remarks) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (statuses == null || statuses.isEmpty()) return 0;


        List<Long> studentIds = new ArrayList<>(statuses.keySet());
        Map<Long, Student> studentMap = studentRepository.findAllById(studentIds)
                .stream().collect(Collectors.toMap(Student::getStudentId, Function.identity()));


        List<Attendance> existingList = attendanceRepository.findBySubjectAndDate(subjectId, date);
        Map<Long, Attendance> existingMap = existingList.stream()
                .collect(Collectors.toMap(
                        a -> a.getStudent().getStudentId(),
                        Function.identity()));

        List<Attendance> toSave = new ArrayList<>();
        for (Map.Entry<Long, String> entry : statuses.entrySet()) {
            Long studentId = entry.getKey();
            String status  = entry.getValue();
            if (status == null || status.isBlank()) continue;

            Student student = studentMap.get(studentId);
            if (student == null) continue; // skip unknown students silently

            AttendanceId aid = new AttendanceId(studentId, subjectId, date);
            Attendance att = existingMap.getOrDefault(studentId, new Attendance());
            att.setId(aid);
            att.setStudent(student);
            att.setSubject(subject);
            att.setStatus(status);
            att.setRemarks(remarks != null ? remarks.getOrDefault(studentId, "") : "");
            toSave.add(att);
        }
        attendanceRepository.saveAll(toSave);
        return toSave.size();
    }

    public double getAttendancePercentageBySubject(Long studentId, Long subjectId) {
        long total = attendanceRepository.countTotalByStudentAndSubject(studentId, subjectId);
        if (total == 0) return 0.0;
        long present = attendanceRepository.countPresentByStudentAndSubject(studentId, subjectId);
        return Math.round((present * 100.0 / total) * 10.0) / 10.0;
    }
}
