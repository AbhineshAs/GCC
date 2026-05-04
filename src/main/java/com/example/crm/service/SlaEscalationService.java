package com.example.crm.service;

import com.example.crm.entity.LeaveRequest;
import com.example.crm.entity.User;
import com.example.crm.repository.LeaveRepository;
import com.example.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SlaEscalationService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    // This runs automatically every hour (3600000 milliseconds)
    // For testing, you can change it to 60000 (1 minute) to see it work immediately
    @Scheduled(fixedRate = 3600000) 
    public void checkLeaveSlaBreaches() {
        System.out.println("--- SLA WATCHER: Checking for breached workflows ---");

        // Define SLA: 48 hours ago
        LocalDateTime slaLimit = LocalDateTime.now().minusHours(48);

        // Find leaves: PENDING, Not Escalated, Older than 48 hours
        List<LeaveRequest> breachedLeaves = leaveRepository
                .findByStatusAndIsEscalatedFalseAndCreatedAtBefore("PENDING", slaLimit);

        if (breachedLeaves.isEmpty()) {
            return; // Everything is healthy, do nothing.
        }

        // Get all ADMINs to notify them
        List<User> admins = userRepository.findByRole("ADMIN");

        for (LeaveRequest leave : breachedLeaves) {
            // 1. Mark as Escalated
            leave.setIsEscalated(true);
            leaveRepository.save(leave);

            // 2. Notify Admins
            for (User admin : admins) {
                if (admin.getEmail() != null) {
                    sendEscalationEmail(admin.getEmail(), leave);
                }
            }
            
            System.out.println("ESCALATED: Leave ID " + leave.getId() + " (Manager failed to respond in 48h)");
        }
    }

    private void sendEscalationEmail(String adminEmail, LeaveRequest leave) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = 
                new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(adminEmail);
            helper.setSubject("URGENT: SLA Breach - Pending Leave Request");
            
            String htmlMsg = "<h2 style='color: #f43f5e;'>SLA Escalation Alert</h2>" +
                    "<p>A leave request has exceeded the 48-hour SLA limit and requires Admin intervention.</p>" +
                    "<ul>" +
                    "<li><b>Employee:</b> " + leave.getUser().getName() + "</li>" +
                    "<li><b>Manager:</b> " + (leave.getUser().getManager() != null ? leave.getUser().getManager().getName() : "None") + "</li>" +
                    "<li><b>Duration:</b> " + leave.getStartDate() + " to " + leave.getEndDate() + "</li>" +
                    "</ul>" +
                    "<p>Please log in to the Admin Dashboard to review.</p>";

            helper.setText(htmlMsg, true);
            helper.setFrom("your-system-email@gmail.com");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Escalation Mail Error: " + e.getMessage());
        }
    }
}