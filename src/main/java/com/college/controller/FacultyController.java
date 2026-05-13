package com.college.controller;

import com.college.model.*;
import com.college.repository.*;
import com.college.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired private FacultyService facultyService;
    @Autowired private SubjectService subjectService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private ExamService examService;
    @Autowired private ResultService resultService;
    @Autowired private StudentService studentService;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private StudentRepository studentRepository;

    private Faculty getLoggedFaculty(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        String role  = (String) session.getAttribute("userRole");
        if (!"FACULTY".equals(role) || email == null) return null;
        return facultyService.getFacultyByEmail(email).orElse(null);
    }

    // ── DASHBOARD ───────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Faculty faculty = getLoggedFaculty(session);
        if (faculty == null) return "redirect:/login";

        Long fid = faculty.getFacultyId();
        // FIX: subjectService.getSubjectsByFaculty already sets studentCount via DB query
        List<Subject> subjects = subjectService.getSubjectsByFaculty(fid);

        // FIX: sum studentCount from enriched subjects — no N+1 queries
        long totalStudents = subjects.stream().mapToLong(Subject::getStudentCount).sum();

        model.addAttribute("faculty",       faculty);
        model.addAttribute("subjectCount",  subjects.size());
        model.addAttribute("examCount",     facultyService.getExamCountByFaculty(fid));
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("subjects",      subjects);
        return "faculty/dashboard";
    }

    // ── ATTENDANCE: Step 1 — Pick Subject + Date ────────────
    @GetMapping("/attendance")
    public String attendancePage(HttpSession session, Model model) {
        Faculty faculty = getLoggedFaculty(session);
        if (faculty == null) return "redirect:/login";
        model.addAttribute("faculty",  faculty);
        model.addAttribute("subjects", subjectService.getSubjectsByFaculty(faculty.getFacultyId()));
        model.addAttribute("today",    LocalDate.now().toString());
        model.addAttribute("students", List.of());
        return "faculty/attendance";
    }

    // ── ATTENDANCE: Step 2 — Load students for subject+date ─
    @GetMapping("/attendance/load")
    public String loadStudentsForAttendance(@RequestParam Long subjectId,
                                            @RequestParam(required = false) String date,
                                            HttpSession session, Model model) {
        Faculty faculty = getLoggedFaculty(session);
        if (faculty == null) return "redirect:/login";

        LocalDate attendanceDate = (date != null && !date.isBlank())
                ? LocalDate.parse(date) : LocalDate.now();

        List<Student> students = studentService.getStudentsBySubject(subjectId);

        // Pre-fill existing attendance for re-marking
        List<Attendance> existing = attendanceService.getAttendanceBySubjectAndDate(subjectId, attendanceDate);
        Map<Long, String> existingStatus  = new HashMap<>();
        Map<Long, String> existingRemarks = new HashMap<>();
        for (Attendance a : existing) {
            existingStatus.put(a.getStudent().getStudentId(), a.getStatus());
            existingRemarks.put(a.getStudent().getStudentId(), a.getRemarks());
        }

        model.addAttribute("faculty",           faculty);
        model.addAttribute("subjects",          subjectService.getSubjectsByFaculty(faculty.getFacultyId()));
        model.addAttribute("selectedSubjectId", subjectId);
        model.addAttribute("selectedDate",      attendanceDate.toString());
        model.addAttribute("students",          students);
        model.addAttribute("existingStatus",    existingStatus);
        model.addAttribute("existingRemarks",   existingRemarks);
        model.addAttribute("today",             LocalDate.now().toString());
        return "faculty/attendance";
    }

    // ── ATTENDANCE: Step 3 — BATCH SAVE ─────────────────────
    @PostMapping("/attendance/batch")
    public String saveBatchAttendance(
            @RequestParam Long subjectId,
            @RequestParam String attendanceDate,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes ra) {
        try {
            LocalDate date = LocalDate.parse(attendanceDate);
            Map<Long, String> statuses = new HashMap<>();
            Map<Long, String> remarks  = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("status_")) {
                    statuses.put(Long.parseLong(key.substring(7)), entry.getValue());
                } else if (key.startsWith("remarks_")) {
                    remarks.put(Long.parseLong(key.substring(8)), entry.getValue());
                }
            }
            int count = attendanceService.saveAttendanceBatch(subjectId, date, statuses, remarks);
            ra.addFlashAttribute("success", "✅ Attendance saved for " + count + " students.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error saving attendance: " + e.getMessage());
        }
        return "redirect:/faculty/attendance";
    }

    // ── MY STUDENTS ─────────────────────────────────────────
    @GetMapping("/my-students")
    public String myStudents(HttpSession session, Model model) {
        Faculty faculty = getLoggedFaculty(session);
        if (faculty == null) return "redirect:/login";

        // FIX: Use enriched subjects from subjectService
        List<Subject> subjects = subjectService.getSubjectsByFaculty(faculty.getFacultyId());
        Map<Subject, List<Student>> subjectStudentMap = new LinkedHashMap<>();
        for (Subject s : subjects) {
            subjectStudentMap.put(s, studentService.getStudentsBySubject(s.getSubjectId()));
        }
        model.addAttribute("faculty",           faculty);
        model.addAttribute("subjectStudentMap", subjectStudentMap);
        return "faculty/my-students";
    }

    // ── EXAMS ───────────────────────────────────────────────
    @GetMapping("/exams")
    public String examsPage(HttpSession session, Model model) {
        Faculty faculty = getLoggedFaculty(session);
        if (faculty == null) return "redirect:/login";
        model.addAttribute("faculty",  faculty);
        model.addAttribute("exams",    examService.getExamsByFaculty(faculty.getFacultyId()));
        model.addAttribute("subjects", subjectService.getSubjectsByFaculty(faculty.getFacultyId()));
        return "faculty/exams";
    }

    @PostMapping("/exams/add")
    public String addExam(@RequestParam Long subjectId,
                          @RequestParam String examType,
                          @RequestParam String examDate,
                          RedirectAttributes ra) {
        try {
            Exam exam = new Exam();
            subjectRepository.findById(subjectId).ifPresent(exam::setSubject);
            exam.setExamType(examType);
            exam.setExamDate(LocalDate.parse(examDate));
            examService.saveExam(exam);
            ra.addFlashAttribute("success", "Exam scheduled.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/faculty/exams";
    }

    // ── RESULTS ─────────────────────────────────────────────
    @GetMapping("/results")
    public String resultsPage(HttpSession session, Model model) {
        Faculty faculty = getLoggedFaculty(session);
        if (faculty == null) return "redirect:/login";
        model.addAttribute("faculty",  faculty);
        model.addAttribute("exams",    examService.getExamsByFaculty(faculty.getFacultyId()));
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("results",  resultService.getAllResults());
        return "faculty/results";
    }

    @PostMapping("/results/add")
    public String addResult(@RequestParam Long studentId,
                            @RequestParam Long examId,
                            @RequestParam Double marks,
                            RedirectAttributes ra) {
        try {
            resultService.saveResult(studentId, examId, marks);
            ra.addFlashAttribute("success", "Result saved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/faculty/results";
    }

    // ── PROFILE ─────────────────────────────────────────────
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Faculty faculty = getLoggedFaculty(session);
        if (faculty == null) return "redirect:/login";
        model.addAttribute("faculty", faculty);
        return "faculty/profile";
    }
}
