package com.college.service;

import com.college.model.Fees;
import com.college.model.Student;
import com.college.repository.FeesRepository;
import com.college.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FeesService {

    @Autowired
    private FeesRepository feesRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Fees> getAllFees() {
        return feesRepository.findAll();
    }

    public List<Fees> getFeesByStudent(Long studentId) {
        return feesRepository.findByStudentStudentId(studentId);
    }

    public Optional<Fees> getFeesById(Long id) {
        return feesRepository.findById(id);
    }

    public Fees saveFees(Fees fees) {
        return feesRepository.save(fees);
    }

    public Fees updateFeeStatus(Long id, String status) {
        Fees fees = feesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee record not found"));
        fees.setPaymentStatus(status);
        return feesRepository.save(fees);
    }

    public void deleteFees(Long id) {
        feesRepository.deleteById(id);
    }

    public long countPendingFees() {
        return feesRepository.countByPaymentStatus("PENDING");
    }
}
