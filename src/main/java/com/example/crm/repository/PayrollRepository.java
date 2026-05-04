package com.example.crm.repository;

import com.example.crm.entity.Payroll;
import com.example.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    Optional<Payroll> findByUserAndMonthAndYear(User user, Integer month, Integer year);
}

