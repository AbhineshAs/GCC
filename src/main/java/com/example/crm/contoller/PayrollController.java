package com.example.crm.contoller;

import com.example.crm.entity.Payroll;
import com.example.crm.entity.User;
import com.example.crm.repository.PayrollRepository;
import com.example.crm.repository.UserRepository;
import com.example.crm.service.PayrollService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
@Controller
public class PayrollController {

    @Autowired 
    private PayrollService payrollService;
    
    @Autowired 
    private PayrollRepository payrollRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private com.example.crm.repository.EmployeeProfileRepository employeeProfileRepository;

    // --- ADMIN VIEW: Handles HR & Managers ---
    @GetMapping("/admin/payroll")
    public String adminPayrollView(@RequestParam(required = false) Integer month,@RequestParam(required = false) Integer year,HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"ADMIN".equals(admin.getRole())) return "redirect:/login";

        LocalDate now = LocalDate.now();
        
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();
        
        
        
        // FIXED: Updated roles to match your database (SALES_MANAGER)
        List<Payroll> managementPayrolls = payrollRepository.findAll().stream()
                .filter(p -> p.getMonth() == now.getMonthValue() && p.getYear() == now.getYear())
                .filter(p -> "HR".equals(p.getUser().getRole()) || 
                            "SALES_MANAGER".equals(p.getUser().getRole()) || 
                            "MANAGER".equals(p.getUser().getRole())) 
                .sorted(Comparator.comparing(p -> p.getUser().getName()))
                .toList();

        model.addAttribute("payrolls", managementPayrolls);
        model.addAttribute("currentCycle", java.time.Month.of(targetMonth).name() + " " + targetYear);
        model.addAttribute("currentMonth", targetMonth);
        // Timer logic support
        model.addAttribute("checkInTime", session.getAttribute("checkInTime"));
        
        return "admin_payroll_terminal";
    }

    // --- HR VIEW: Handles Executives ---
    @GetMapping("/hr/payroll")
    public String hrPayrollView(@RequestParam(required = false) Integer month,
    							@RequestParam(required = false) Integer year,
    							HttpSession session, Model model) {
        User hr = (User) session.getAttribute("user");
        if (hr == null || !"HR".equals(hr.getRole())) return "redirect:/login";

        LocalDate now = LocalDate.now();
        
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();
        
        // FIXED: Updated roles to match your database (SALES_EXECUTIVE)
        List<Payroll> executivePayrolls = payrollRepository.findAll().stream()
                .filter(p -> p.getMonth() == now.getMonthValue() && p.getYear() == now.getYear())
                .filter(p -> "SALES_EXECUTIVE".equals(p.getUser().getRole()) || 
                            "EXECUTIVE".equals(p.getUser().getRole()))
                .sorted(Comparator.comparing(p -> p.getUser().getName()))
                .toList();

        model.addAttribute("payrolls", executivePayrolls);
        model.addAttribute("currentCycle", java.time.Month.of(targetMonth).name() + " " + targetYear);
        model.addAttribute("currentMonth", targetMonth);
        
        
        // Timer logic support
        model.addAttribute("checkInTime", session.getAttribute("checkInTime"));
        
        return "hr_payroll_terminal";
    }
    
    

    // Global Processing Logic
    @PostMapping("/payroll/generate")
    public String generateBatch(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        LocalDate now = LocalDate.now();
        payrollService.processMonthlyPayroll(now.getMonthValue(), now.getYear());
        
        // Redirect back to the correct dashboard based on role
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/payroll?success=batch_completed";
        }
        return "redirect:/hr/payroll?success=batch_completed";
    }
    
 // Add this to PayrollController.java
    @PostMapping("/hr/payroll/update-profile")
    public String hrUpdateEmployeeProfile(@RequestParam Long userId,
                                          @RequestParam Double basicSalary,
                                          @RequestParam(required = false) Double housingAllowance,
                                          @RequestParam(required = false) Double transportationAllowance,
                                          @RequestParam(required = false) Double lopDeduction) {
        
        // 1. Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 2. Find or create the profile (Using your existing repo)
        com.example.crm.entity.EmployeeProfile profile = employeeProfileRepository.findByUser(user)
                .orElse(new com.example.crm.entity.EmployeeProfile());
        
        // 3. Update the values
        profile.setUser(user);
        profile.setBasicSalary(basicSalary);
        profile.setHousingAllowance(housingAllowance != null ? housingAllowance : 0.0);
        profile.setTransportationAllowance(transportationAllowance != null ? transportationAllowance : 0.0);
        
     // SAVE THE LOP VALUE TO THE PROFILE
        profile.setLopDeduction(lopDeduction != null ? lopDeduction : 0.0);
        
        // 4. Save
        employeeProfileRepository.save(profile);
        
        // 5. Redirect back to the HR payroll page
        return "redirect:/hr/payroll?success=salary_updated";
    }
    
    @GetMapping("/payroll/download-slip/{id}")
    public void downloadSlip(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Payroll p = payrollRepository.findById(id).orElseThrow();
        
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Salary_Slip_" + p.getUser().getName().replace(" ", "_") + ".pdf";
        response.setHeader(headerKey, headerValue);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Fonts
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font fontTableHead = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        // Header
        Paragraph title = new Paragraph("GCC SMART AUTOMATION", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        Paragraph subTitle = new Paragraph("MONTHLY SALARY STATEMENT", fontSub);
        subTitle.setAlignment(Paragraph.ALIGN_CENTER);
        subTitle.setSpacingAfter(20);
        document.add(subTitle);

        // Employee Info Table
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.addCell("Employee Name: " + p.getUser().getName());
        infoTable.addCell("Payroll Cycle: " + p.getMonth() + "/" + p.getYear());
        infoTable.addCell("Role: " + p.getUser().getRole());
        infoTable.addCell("Status: " + p.getStatus());
        infoTable.setSpacingAfter(20);
        document.add(infoTable);

        // Financial Breakdown Table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {3.0f, 2.0f});

        // Header Row
        PdfPCell h1 = new PdfPCell(new Phrase("Description", fontTableHead));
        PdfPCell h2 = new PdfPCell(new Phrase("Amount (SAR)", fontTableHead));
        h1.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        h2.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        table.addCell(h1);
        table.addCell(h2);

     // Data Rows
        table.addCell("Basic Salary");
        table.addCell(String.format("%,.2f", p.getBasicSalary()));

        table.addCell("Total Allowances (Housing/Transport/Other)");
        table.addCell(String.format("%,.2f", p.getTotalAllowances()));

        table.addCell("GOSI Deduction (10%)");
        table.addCell("-" + String.format("%,.2f", p.getGosiDeduction()));

        table.addCell("Loss of Pay (LOP)");
        table.addCell("-" + String.format("%,.2f", p.getLopDeduction()));

     // Total Row
        PdfPCell totalLab = new PdfPCell(new Phrase("NET PAYABLE", fontTableHead));
        PdfPCell totalVal = new PdfPCell(new Phrase("SAR " + String.format("%,.2f", p.getNetSalary()), fontTableHead));
        totalVal.setBackgroundColor(java.awt.Color.YELLOW);
        table.addCell(totalLab);
        table.addCell(totalVal);
        document.add(table);

        Paragraph footer = new Paragraph("\n\nThis is a system-generated document and does not require a signature.", 
                                         FontFactory.getFont(FontFactory.HELVETICA, 8));
        footer.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }
    
 // Add this new method to PayrollController.java
    @PostMapping("/admin/payroll/update-profile")
    public String adminUpdateEmployeeProfile(@RequestParam Long userId,
                                          @RequestParam Double basicSalary,
                                          @RequestParam(required = false) Double housingAllowance,
                                          @RequestParam(required = false) Double transportationAllowance,
                                          @RequestParam(required = false) Double lopDeduction,
                                          @RequestParam(required = false) Double otherAllowances) {
        
        // 1. Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 2. Find or create the profile
        com.example.crm.entity.EmployeeProfile profile = employeeProfileRepository.findByUser(user)
                .orElse(new com.example.crm.entity.EmployeeProfile());
        
        // 3. Update the values
        profile.setUser(user);
        profile.setBasicSalary(basicSalary);
        profile.setHousingAllowance(housingAllowance != null ? housingAllowance : 0.0);
        profile.setTransportationAllowance(transportationAllowance != null ? transportationAllowance : 0.0);
        profile.setOtherAllowances(otherAllowances != null ? otherAllowances : 0.0);
        profile.setLopDeduction(lopDeduction != null ? lopDeduction : 0.0);
        
        // 4. Save
        employeeProfileRepository.save(profile);
        
        // 5. REDIRECT back to the ADMIN page (This stops the login redirect)
        return "redirect:/admin/payroll?success=salary_updated";
    }
}