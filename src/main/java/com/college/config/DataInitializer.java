package com.college.config;

import com.college.model.User;
import com.college.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds default ADMIN user on first startup.
 * Login: admin@college.com / admin123
 *
 * NOTE: Subject.subjectType is now a required column.
 * If you have existing subjects without this column, run:
 *   ALTER TABLE subject ADD COLUMN subject_type VARCHAR(20) NOT NULL DEFAULT 'COMPULSORY';
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@college.com")) {
            User admin = new User("admin@college.com", "admin123", User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Default admin created: admin@college.com / admin123");
        }
    }
}
