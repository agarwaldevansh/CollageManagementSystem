package com.college.service;

import com.college.model.Department;
import com.college.repository.CourseRepository;
import com.college.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, Department updated) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        dept.setDepartmentName(updated.getDepartmentName());
        dept.setHod(updated.getHod());
        return departmentRepository.save(dept);
    }

    public void deleteDepartment(Long id) {
        long courseCount = courseRepository.countByDepartmentDepartmentId(id);
        if (courseCount > 0) {
            throw new RuntimeException("Cannot delete department with existing courses.");
        }
        departmentRepository.deleteById(id);
    }

    public long getTotalDepartments() {
        return departmentRepository.count();
    }

    /** Validate: each department must have 3–5 courses */
    public boolean isValidCourseCount(Long departmentId) {
        long count = courseRepository.countByDepartmentDepartmentId(departmentId);
        return count >= 3 && count <= 5;
    }
}
