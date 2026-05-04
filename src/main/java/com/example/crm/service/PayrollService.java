package com.example.crm.service;

import com.example.crm.entity.*;
import com.example.crm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PayrollService {

    @Autowired private PayrollRepository payrollRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeProfileRepository profileRepository;

    public void processMonthlyPayroll(int month, int year) {
        // 1. Get ALL users to ensure we don't miss anyone
        List<User> allUsers = userRepository.findAll();

     // Inside PayrollService.java -> processMonthlyPayroll method
        for (User user : allUsers) {
            EmployeeProfile profile = profileRepository.findByUser(user).orElse(null);
            
            double basic = 0.0;
            double housing = 0.0;
            double transport = 0.0;
            double other = 0.0;
            double lop = 0.0;

            if (profile != null) {
                basic = (profile.getBasicSalary() != null) ? profile.getBasicSalary() : 0.0;
                housing = (profile.getHousingAllowance() != null) ? profile.getHousingAllowance() : 0.0;
                transport = (profile.getTransportationAllowance() != null) ? profile.getTransportationAllowance() : 0.0;
                other = (profile.getOtherAllowances() != null) ? profile.getOtherAllowances() : 0.0;
                lop = (profile.getLopDeduction() != null) ? profile.getLopDeduction() : 0.0;
            }

            double gosi = basic * 0.10;
            
            // NEW CALCULATION LOGIC
            double grossSalary = basic + housing + transport + other;
            double netSalary = grossSalary - (gosi + lop);

            Payroll payroll = payrollRepository.findByUserAndMonthAndYear(user, month, year)
                                .orElse(new Payroll());

            payroll.setUser(user);
            payroll.setMonth(month);
            payroll.setYear(year);
            payroll.setBasicSalary(basic);
            payroll.setTotalAllowances(housing + transport + other); // Store sum of allowances
            payroll.setGosiDeduction(gosi);
            payroll.setLopDeduction(lop);
            payroll.setNetSalary(netSalary);
            payroll.setStatus("GENERATED");
            payroll.setProcessedAt(LocalDateTime.now());

            payrollRepository.save(payroll);
        }
    }
}