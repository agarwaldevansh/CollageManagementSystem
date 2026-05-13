package com.college.controller;

import com.college.model.Student;
import com.college.model.Subject;
import com.college.repository.SubjectRepository;
import com.college.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private ResultService resultService;
    @Autowired private FeesService feesService;
    @Autowired private SubjectService subjectService;
    @Autowired private SubjectRepository subjectRepository;

    private Student getLoggedStudent(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        String role  = (String) session.getAttribute("userRole");
        if (!"STUDENT".equals(role) || email == null) return null;
        return studentService.getStudentByEmail(email).orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";
        Long sid = student.getStudentId();
        model.addAttribute("student",       student);
        model.addAttribute("currentYear",   student.getCurrentYear());
        model.addAttribute("attendancePct", studentService.getAttendancePercentage(sid));
        model.addAttribute("results",       resultService.getResultsByStudent(sid));
        model.addAttribute("feesList",      feesService.getFeesByStudent(sid));
        model.addAttribute("subjects",      student.getEnrolledSubjects());
        model.addAttribute("subjectCount",  student.getEnrolledSubjects().size());
        return "student/dashboard";
    }

    @GetMapping("/attendance")
    public String attendance(HttpSession session, Model model) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";
        Long sid = student.getStudentId();
        double pct = studentService.getAttendancePercentage(sid);
        model.addAttribute("student",        student);
        model.addAttribute("currentYear",    student.getCurrentYear());
        model.addAttribute("attendanceList", attendanceService.getAttendanceByStudent(sid));
        model.addAttribute("attendancePct",  pct);
        model.addAttribute("attendanceStatus",
                pct >= 75 ? "GOOD" : (pct >= 60 ? "WARNING" : "DANGER"));
        return "student/attendance";
    }

    @GetMapping("/results")
    public String results(HttpSession session, Model model) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";
        model.addAttribute("student",     student);
        model.addAttribute("currentYear", student.getCurrentYear());
        model.addAttribute("results",     resultService.getResultsByStudent(student.getStudentId()));
        return "student/results";
    }

    @GetMapping("/fees")
    public String fees(HttpSession session, Model model) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";
        model.addAttribute("student",     student);
        model.addAttribute("currentYear", student.getCurrentYear());
        model.addAttribute("feesList",    feesService.getFeesByStudent(student.getStudentId()));
        return "student/fees";
    }

    @GetMapping("/timetable")
    public String timetable(HttpSession session, Model model) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";
        model.addAttribute("student",     student);
        model.addAttribute("currentYear", student.getCurrentYear());
        model.addAttribute("subjects",    student.getEnrolledSubjects());
        return "student/timetable";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";
        model.addAttribute("student",     student);
        model.addAttribute("currentYear", student.getCurrentYear());
        return "student/profile";
    }

    // ── ENROLL: shows compulsory (locked) + available electives ──
    @GetMapping("/enroll")
    public String enrollPage(HttpSession session, Model model) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";

        List<Subject> enrolled = student.getEnrolledSubjects();

        // Electives available (not yet enrolled)
        List<Subject> availableElectives = (student.getCourse() != null)
                ? subjectRepository.findAvailableElectivesForStudent(
                        student.getCourse().getCourseId(), student.getStudentId())
                : List.of();

        model.addAttribute("student",           student);
        model.addAttribute("currentYear",       student.getCurrentYear());
        model.addAttribute("enrolledSubjects",  enrolled);
        model.addAttribute("availableElectives", availableElectives);
        return "student/enroll";
    }

    @PostMapping("/enroll")
    public String enrollSubmit(@RequestParam Long subjectId,
                               HttpSession session, RedirectAttributes ra) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";
        try {
            studentService.enrollInSubject(student.getStudentId(), subjectId);
            ra.addFlashAttribute("success", "Enrolled successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/enroll";
    }

    @PostMapping("/enroll/drop")
    public String dropSubject(@RequestParam Long subjectId,
                              HttpSession session, RedirectAttributes ra) {
        Student student = getLoggedStudent(session);
        if (student == null) return "redirect:/login";
        try {
            studentService.dropSubject(student.getStudentId(), subjectId);
            ra.addFlashAttribute("success", "Subject dropped.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/enroll";
    }
}
