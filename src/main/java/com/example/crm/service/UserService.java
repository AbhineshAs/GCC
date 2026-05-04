package com.example.crm.service;

import com.example.crm.entity.EmployeeProfile;
import com.example.crm.entity.User;
import com.example.crm.repository.EmployeeProfileRepository;
import com.example.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeProfileRepository profileRepository;

    @Transactional
    public User registerNewUser(User user) {
        // 1. Save the User to get an ID
        User savedUser = userRepository.save(user);

        // 2. Automatically create the Financial/HR Profile
        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(savedUser);
        
        // Initialize with default values so Payroll Batch works
        profile.setBasicSalary(0.0);
        profile.setHousingAllowance(0.0);
        profile.setTransportationAllowance(0.0);
        profile.setOtherAllowances(0.0);
        
        // Set standard leave balances
        profile.setAnnualLeaveBalance(21.0);
        profile.setSickLeaveBalance(15.0);
        profile.setEmergencyLeaveBalance(5.0);
        
        profileRepository.save(profile);

        return savedUser;
    }
}