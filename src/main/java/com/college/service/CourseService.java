package com.college.service;

import com.college.model.Course;
import com.college.repository.CourseRepository;
import com.college.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired private CourseRepository courseRepository;
    @Autowired private DepartmentRepository departmentRepository;

    public List<Course> getAllCourses() { return courseRepository.findAll(); }

    public List<Course> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentDepartmentId(departmentId);
    }

    public Optional<Course> getCourseById(Long id) { return courseRepository.findById(id); }

    public Course saveCourse(Course course) {
        if (course.getDepartment() == null) {
            throw new RuntimeException("Department must be selected for a course.");
        }
        long existing = courseRepository.countByDepartmentDepartmentId(
                course.getDepartment().getDepartmentId());
        if (existing >= 5) {
            throw new RuntimeException("A department cannot have more than 5 courses.");
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updated) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setCourseName(updated.getCourseName());
        course.setDuration(updated.getDuration());
        course.setDepartment(updated.getDepartment());
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public long getTotalCourses() { return courseRepository.count(); }
}
