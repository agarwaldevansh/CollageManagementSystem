package com.college.repository;

import com.college.model.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<Fees, Long> {
    List<Fees> findByStudentStudentId(Long studentId);
    List<Fees> findByPaymentStatus(String paymentStatus);
    long countByPaymentStatus(String paymentStatus);
}
