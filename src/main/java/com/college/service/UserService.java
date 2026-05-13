package com.college.service;

import com.college.model.*;
import com.college.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private FacultyRepository facultyRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private SubjectRepository subjectRepository;

    // ── SIMPLE LOGIN (email + password only) ────────────────
    // DOB / RegNo NOT asked at login — they're stored during signup for records
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    // ── STUDENT SIGNUP — ATOMIC ─────────────────────────────
    // DOB and Registration Number stored in DB; NOT needed for future logins
    @Transactional(rollbackFor = Exception.class)
    public Student registerStudent(String name, String email, String phone, String gender,
                                   String password, LocalDate dob,
                                   Long departmentId, Long courseId,
                                   String address, Integer admissionYear,
                                   String registrationNumber) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered.");
        }
        // Validate unique registration number
        if (registrationNumber != null && !registrationNumber.isBlank()) {
            if (studentRepository.findByRegistrationNumber(registrationNumber).isPresent()) {
                throw new RuntimeException("Registration number already in use.");
            }
        }

        // 1. User
        User user = userRepository.save(new User(email, password, User.Role.STUDENT));

        // 2. Person
        Person person = new Person();
        person.setName(name); person.setEmail(email);
        person.setPhone(phone); person.setGender(gender); person.setDob(dob);

        // 3. Student
        Student student = new Student();
        student.setUser(user);
        student.setPerson(person);
        student.setAddress(address);
        student.setAdmissionYear(admissionYear != null ? admissionYear : Year.now().getValue());

        // 4. RegNo: user-provided or auto-generate
        String regNo = (registrationNumber != null && !registrationNumber.isBlank())
                ? registrationNumber.trim().toUpperCase()
                : "STU-" + Year.now().getValue() + "-" + (System.currentTimeMillis() % 100000);
        student.setRegistrationNumber(regNo);

        // 5. Department + Course
        if (departmentId != null)
            departmentRepository.findById(departmentId).ifPresent(student::setDepartment);
        if (courseId != null)
            courseRepository.findById(courseId).ifPresent(student::setCourse);

        // 6. Save student (cascades Person)
        Student saved = studentRepository.save(student);

        // 7. AUTO-ENROLL in compulsory subjects of the assigned course
        if (saved.getCourse() != null) {
            List<Subject> compulsory = subjectRepository.findByCourseCourseIdAndSubjectType(
                    saved.getCourse().getCourseId(), "COMPULSORY");
            saved.getEnrolledSubjects().addAll(compulsory);
            saved = studentRepository.save(saved);
        }

        System.out.println("✅ Student registered. RegNo: " + regNo);
        return saved;
    }

    // ── FACULTY SIGNUP — ATOMIC ─────────────────────────────
    // facultyId (employeeId) stored in DB; NOT needed for future logins
    @Transactional(rollbackFor = Exception.class)
    public Faculty registerFaculty(String name, String email, String phone, String gender,
                                   String password, LocalDate dob,
                                   String qualification, String experience,
                                   Long departmentId, String facultyIdInput) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered.");
        }
        // Validate unique faculty ID
        if (facultyIdInput != null && !facultyIdInput.isBlank()) {
            if (facultyRepository.findByEmployeeId(facultyIdInput.trim()).isPresent()) {
                throw new RuntimeException("Faculty ID already in use.");
            }
        }

        User user = userRepository.save(new User(email, password, User.Role.FACULTY));

        Person person = new Person();
        person.setName(name); person.setEmail(email);
        person.setPhone(phone); person.setGender(gender); person.setDob(dob);

        Faculty faculty = new Faculty();
        faculty.setUser(user);
        faculty.setPerson(person);
        faculty.setQualification(qualification);
        faculty.setExperience(experience);

        String empId = (facultyIdInput != null && !facultyIdInput.isBlank())
                ? facultyIdInput.trim().toUpperCase()
                : "FAC-" + Year.now().getValue() + "-" + (System.currentTimeMillis() % 100000);
        faculty.setEmployeeId(empId);

        if (departmentId != null)
            departmentRepository.findById(departmentId).ifPresent(faculty::setDepartment);

        Faculty saved = facultyRepository.save(faculty);
        System.out.println("✅ Faculty registered. EmpID: " + empId);
        return saved;
    }

    public boolean existsByEmail(String email) { return userRepository.existsByEmail(email); }
    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }
}
