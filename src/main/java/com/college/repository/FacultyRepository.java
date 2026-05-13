package com.college.repository;

import com.college.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByPersonEmail(String email);
    List<Faculty> findByDepartmentDepartmentId(Long departmentId);
    Optional<Faculty> findByEmployeeId(String employeeId);
}
