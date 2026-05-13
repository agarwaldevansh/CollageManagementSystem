package com.college.repository;

import com.college.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByDepartmentDepartmentId(Long departmentId);
    long countByDepartmentDepartmentId(Long departmentId);
}
