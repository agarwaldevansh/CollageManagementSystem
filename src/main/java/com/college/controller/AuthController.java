package com.college.controller;

import com.college.model.User;
import com.college.repository.CourseRepository;
import com.college.repository.DepartmentRepository;
import com.college.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private CourseRepository courseRepository;

    @GetMapping("/")
    public String home() { return "redirect:/login"; }

    // ── LOGIN: email + password ONLY ────────────────────────
    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Optional<User> userOpt = userService.login(email, password);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
            return "login";
        }
        User user = userOpt.get();
        session.setAttribute("loggedUser",  user);
        session.setAttribute("userEmail",   user.getEmail());
        session.setAttribute("userRole",    user.getRole().name());
        return switch (user.getRole()) {
            case ADMIN   -> "redirect:/admin/dashboard";
            case STUDENT -> "redirect:/student/dashboard";
            case FACULTY -> "redirect:/faculty/dashboard";
        };
    }

    // ── SIGNUP ───────────────────────────────────────────────
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("courses",     courseRepository.findAll());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String phone,
                         @RequestParam String gender,
                         @RequestParam String password,
                         @RequestParam String role,
                         @RequestParam(required = false) String dob,
                         @RequestParam(required = false) Long departmentId,
                         @RequestParam(required = false) Long courseId,
                         @RequestParam(required = false) String qualification,
                         @RequestParam(required = false) String experience,
                         @RequestParam(required = false) Integer admissionYear,
                         @RequestParam(required = false) String address,
                         @RequestParam(required = false) String registrationNumber,
                         @RequestParam(required = false) String facultyIdInput,
                         RedirectAttributes ra) {
        try {
            LocalDate dobDate = null;
            if (dob != null && !dob.isBlank()) {
                try { dobDate = LocalDate.parse(dob); }
                catch (Exception e) { throw new RuntimeException("Invalid DOB format."); }
            }
            User.Role userRole = User.Role.valueOf(role.toUpperCase());

            if (userRole == User.Role.STUDENT) {
                var student = userService.registerStudent(
                        name, email, phone, gender, password, dobDate,
                        departmentId, courseId, address, admissionYear, registrationNumber);
                ra.addFlashAttribute("success",
                        "✅ Registered! Your Registration No: "
                        + student.getRegistrationNumber()
                        + " — Login now with your email & password.");

            } else if (userRole == User.Role.FACULTY) {
                var faculty = userService.registerFaculty(
                        name, email, phone, gender, password, dobDate,
                        qualification, experience, departmentId, facultyIdInput);
                ra.addFlashAttribute("success",
                        "✅ Registered! Your Faculty ID: "
                        + faculty.getEmployeeId()
                        + " — Login now with your email & password.");
            } else {
                ra.addFlashAttribute("error", "Admin accounts cannot self-register.");
                return "redirect:/signup";
            }
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/signup";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
