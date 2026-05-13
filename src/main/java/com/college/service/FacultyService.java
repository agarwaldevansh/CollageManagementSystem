package com.college.service;

import com.college.model.Faculty;
import com.college.repository.ExamRepository;
import com.college.repository.FacultyRepository;
import com.college.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    @Autowired private FacultyRepository facultyRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private ExamRepository examRepository;

    public List<Faculty> getAllFaculty() { return facultyRepository.findAll(); }
    public Optional<Faculty> getFacultyById(Long id) { return facultyRepository.findById(id); }
    public Optional<Faculty> getFacultyByEmail(String email) {
        return facultyRepository.findByPersonEmail(email);
    }
    public Faculty saveFaculty(Faculty faculty) { return facultyRepository.save(faculty); }

    public Faculty updateFaculty(Long id, Faculty updated) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        faculty.getPerson().setName(updated.getPerson().getName());
        faculty.getPerson().setPhone(updated.getPerson().getPhone());
        faculty.setQualification(updated.getQualification());
        faculty.setExperience(updated.getExperience());
        faculty.setDepartment(updated.getDepartment());
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) { facultyRepository.deleteById(id); }
    public long getTotalFaculty() { return facultyRepository.count(); }

    public long getSubjectCountByFaculty(Long facultyId) {
        return subjectRepository.countByFacultyFacultyId(facultyId);
    }

    public long getExamCountByFaculty(Long facultyId) {
        return examRepository.countBySubjectFacultyFacultyId(facultyId);
    }
}
