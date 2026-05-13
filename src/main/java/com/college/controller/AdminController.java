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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private StudentService studentService;
    @Autowired private FacultyService facultyService;
    @Autowired private DepartmentService departmentService;
    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private FeesService feesService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private ResultService resultService;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private FacultyRepository facultyRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private StudentRepository studentRepository;

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("userRole"));
    }

    // ── DASHBOARD ──────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("totalStudents",     studentService.getTotalStudents());
        model.addAttribute("totalFaculty",      facultyService.getTotalFaculty());
        model.addAttribute("totalCourses",      courseService.getTotalCourses());
        model.addAttribute("totalDepartments",  departmentService.getTotalDepartments());
        model.addAttribute("totalSubjects",     subjectService.getTotalSubjects());
        model.addAttribute("pendingFees",       feesService.countPendingFees());
        model.addAttribute("admissionsPerYear", studentService.getAdmissionsPerYear());
        return "admin/dashboard";
    }

    // ── STUDENTS ───────────────────────────────────────────
    @GetMapping("/students")
    public String students(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("students",    studentService.getAllStudents());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("courses",     courseRepository.findAll());
        return "admin/students";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes ra) {
        try { studentService.deleteStudent(id); ra.addFlashAttribute("success", "Student deleted."); }
        catch (Exception e) { ra.addFlashAttribute("error", "Cannot delete: " + e.getMessage()); }
        return "redirect:/admin/students";
    }

    @GetMapping("/students/{id}/enrollments")
    public String studentEnrollments(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        model.addAttribute("student",          student);
        model.addAttribute("enrolledSubjects", student.getEnrolledSubjects());
        model.addAttribute("course",           student.getCourse());
        return "admin/student-enrollments";
    }

    // ── FACULTY ────────────────────────────────────────────
    @GetMapping("/faculty")
    public String faculty(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("facultyList", facultyService.getAllFaculty());
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/faculty";
    }

    @PostMapping("/faculty/delete/{id}")
    public String deleteFaculty(@PathVariable Long id, RedirectAttributes ra) {
        try { facultyService.deleteFaculty(id); ra.addFlashAttribute("success", "Faculty deleted."); }
        catch (Exception e) { ra.addFlashAttribute("error", "Cannot delete: " + e.getMessage()); }
        return "redirect:/admin/faculty";
    }

    // ── DEPARTMENTS ────────────────────────────────────────
    @GetMapping("/departments")
    public String departments(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/departments";
    }

    @PostMapping("/departments/add")
    public String addDepartment(@RequestParam String departmentName,
                                @RequestParam String hod, RedirectAttributes ra) {
        try {
            Department d = new Department();
            d.setDepartmentName(departmentName); d.setHod(hod);
            departmentService.saveDepartment(d);
            ra.addFlashAttribute("success", "Department '" + departmentName + "' added.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/update/{id}")
    public String updateDepartment(@PathVariable Long id,
                                   @RequestParam String departmentName,
                                   @RequestParam String hod, RedirectAttributes ra) {
        try {
            Department u = new Department();
            u.setDepartmentName(departmentName); u.setHod(hod);
            departmentService.updateDepartment(id, u);
            ra.addFlashAttribute("success", "Department updated.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes ra) {
        try { departmentService.deleteDepartment(id); ra.addFlashAttribute("success", "Department deleted."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/departments";
    }

    // ── COURSES ────────────────────────────────────────────
    @GetMapping("/courses")
    public String courses(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("courses",     courseService.getAllCourses());
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/courses";
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestParam String courseName,
                            @RequestParam String duration,
                            @RequestParam Long departmentId, RedirectAttributes ra) {
        try {
            Course c = new Course();
            c.setCourseName(courseName); c.setDuration(duration);
            departmentRepository.findById(departmentId).ifPresent(c::setDepartment);
            courseService.saveCourse(c);
            ra.addFlashAttribute("success", "Course '" + courseName + "' added.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes ra) {
        try { courseService.deleteCourse(id); ra.addFlashAttribute("success", "Course deleted."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/courses";
    }

    // ── SUBJECTS ───────────────────────────────────────────
    @GetMapping("/subjects")
    public String subjects(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        // FIX: getAllSubjects() now enriches studentCount via DB, no lazy crash
        model.addAttribute("subjects",    subjectService.getAllSubjects());
        model.addAttribute("courses",     courseRepository.findAll());
        model.addAttribute("facultyList", facultyRepository.findAll());
        return "admin/subjects";
    }

    @PostMapping("/subjects/add")
    public String addSubject(@RequestParam String subjectName,
                             @RequestParam(defaultValue = "COMPULSORY") String subjectType,
                             @RequestParam(required = false) Long courseId,
                             @RequestParam(required = false) Long facultyId,
                             RedirectAttributes ra) {
        try {
            // FIX: validate before creating object
            if (courseId == null) {
                ra.addFlashAttribute("error", "Please select a course.");
                return "redirect:/admin/subjects";
            }
            if (facultyId == null) {
                ra.addFlashAttribute("error", "Please assign a faculty member.");
                return "redirect:/admin/subjects";
            }
            Subject s = new Subject();
            s.setSubjectName(subjectName.trim());
            s.setSubjectType(subjectType);
            courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            courseRepository.findById(courseId).ifPresent(s::setCourse);
            facultyRepository.findById(facultyId)
                    .orElseThrow(() -> new RuntimeException("Faculty not found"));
            facultyRepository.findById(facultyId).ifPresent(s::setFaculty);
            subjectService.saveSubject(s);
            ra.addFlashAttribute("success", "Subject '" + subjectName + "' added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error adding subject: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/update/{id}")
    public String updateSubject(@PathVariable Long id,
                                @RequestParam String subjectName,
                                @RequestParam(defaultValue = "COMPULSORY") String subjectType,
                                @RequestParam(required = false) Long courseId,
                                @RequestParam(required = false) Long facultyId,
                                RedirectAttributes ra) {
        try {
            if (courseId == null || facultyId == null) {
                ra.addFlashAttribute("error", "Course and Faculty are required.");
                return "redirect:/admin/subjects";
            }
            Subject s = subjectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            s.setSubjectName(subjectName.trim());
            s.setSubjectType(subjectType);
            courseRepository.findById(courseId).ifPresent(s::setCourse);
            facultyRepository.findById(facultyId).ifPresent(s::setFaculty);
            subjectService.updateSubject(id, s);
            ra.addFlashAttribute("success", "Subject updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error updating subject: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes ra) {
        try { subjectService.deleteSubject(id); ra.addFlashAttribute("success", "Subject deleted."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/subjects";
    }

    // ── REPORTS ────────────────────────────────────────────
    @GetMapping("/attendance")
    public String attendanceReport(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("attendanceList", attendanceService.getAllAttendance());
        return "admin/attendance";
    }

    @GetMapping("/results")
    public String resultsReport(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("results", resultService.getAllResults());
        return "admin/results";
    }

    @GetMapping("/fees")
    public String feesReport(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("feesList", feesService.getAllFees());
        return "admin/fees";
    }

    @PostMapping("/fees/update/{id}")
    public String updateFeeStatus(@PathVariable Long id,
                                  @RequestParam String status, RedirectAttributes ra) {
        try { feesService.updateFeeStatus(id, status); ra.addFlashAttribute("success", "Fee status updated."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/fees";
    }

    // ── FACULTY-SUBJECT MAPPING ────────────────────────────
    // FIX: Build facultySubjectMap in controller — avoids accessing f.subjects (lazy) in template
    @GetMapping("/faculty-mapping")
    public String facultyMapping(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        List<Faculty> facultyList = facultyService.getAllFaculty();

        // Build Map<facultyId, List<Subject>> with enriched student counts
        Map<Long, List<Subject>> facultySubjectMap = new LinkedHashMap<>();
        for (Faculty f : facultyList) {
            List<Subject> subjects = subjectService.getSubjectsByFaculty(f.getFacultyId());
            facultySubjectMap.put(f.getFacultyId(), subjects);
        }

        model.addAttribute("facultyList",       facultyList);
        model.addAttribute("facultySubjectMap", facultySubjectMap);
        // Also pass enriched all-subjects list for overview table
        model.addAttribute("subjects",          subjectService.getAllSubjects());
        return "admin/faculty-mapping";
    }
}
