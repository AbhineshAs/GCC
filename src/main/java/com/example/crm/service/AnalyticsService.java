package com.example.crm.service;

import com.example.crm.entity.Lead;
import com.example.crm.entity.Payroll;
import com.example.crm.repository.LeadRepository;
import com.example.crm.repository.LeaveRepository;
import com.example.crm.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    @Autowired private LeadRepository leadRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private LeaveRepository leaveRepository;
    @Autowired private com.example.crm.repository.TransactionRepository transactionRepository;


    // 1. Total Revenue (From Closed Leads)
    public double calculateTotalRevenue() {
        return leadRepository.findAll().stream()
                .filter(l -> "Close".equalsIgnoreCase(l.getStatus()))
                .mapToDouble(this::extractAmount)
                .sum();
    }

    // 2. Active Pipeline Value (From Non-Closed Leads)
    public double calculateActivePipeline() {
        return leadRepository.findAll().stream()
                .filter(l -> !"Close".equalsIgnoreCase(l.getStatus()))
                .mapToDouble(this::extractAmount)
                .sum();
    }

    // 3. Total Payroll Expense
    public double calculateTotalPayroll() {
        return payrollRepository.findAll().stream()
                .filter(p -> p.getNetSalary() != null)
                .mapToDouble(Payroll::getNetSalary)
                .sum();
    }

    // 4. Urgent SLA Breaches
    public long countSlaBreaches() {
        return leaveRepository.findAll().stream()
                .filter(l -> Boolean.TRUE.equals(l.getIsEscalated()))
                .count();
    }

    // Helper to safely extract money from notes (SAR or ₹)
    private double extractAmount(Lead l) {
        if (l.getNotes() == null) return 0.0;
        try {
            if (l.getNotes().contains("SAR")) {
                return Double.parseDouble(l.getNotes().split("SAR")[1].split("\\|")[0].replaceAll("[^0-9.]", ""));
            } else if (l.getNotes().contains("₹")) {
                return Double.parseDouble(l.getNotes().split("₹")[1].split("\\|")[0].replaceAll("[^0-9.]", ""));
            }
        } catch (Exception e) {
            return 0.0;
        }
        return 0.0;
    }
    
 // Add these to com.example.crm.service.AnalyticsService.java

    // 5. Sales Conversion Rate (Win Rate %)
    public double calculateConversionRate() {
        long totalLeads = leadRepository.count();
        if (totalLeads == 0) return 0.0;
        
        long closedLeads = leadRepository.findAll().stream()
                .filter(l -> "Close".equalsIgnoreCase(l.getStatus()))
                .count();
                
        return ((double) closedLeads / totalLeads) * 100.0;
    }

 // 6. Weighted Revenue Forecast (Predicting future revenue)
    public double calculateForecastedRevenue() {
        return leadRepository.findAll().stream()
                .filter(l -> !"Close".equalsIgnoreCase(l.getStatus())) // Only active leads
                .mapToDouble(l -> {
                    // Try to get amount from notes
                    double amount = extractAmount(l);
                    
                    // FIX: If no specific amount is found, use an Estimated Average Deal Size 
                    // (e.g., SAR 5000) so the prediction works based on the status!
                    if (amount <= 0.0) {
                        amount = 5000.0; 
                    }
                    
                    double probability = getProbability(l.getStatus());
                    return amount * probability;
                })
                .sum();
    }

 // Helper: Assigns win probability based on your exact UI dropdown options
    private double getProbability(String status) {
        if (status == null) return 0.10; 
        
        switch (status.toUpperCase()) {
            case "CONTACT": 
                return 0.20; // 20% chance: Just reached out
            case "INTEREST": 
                return 0.40; // 40% chance: They showed interest
            case "MAYBE BUY": 
                return 0.70; // 70% chance: High intent to purchase
            case "NOT INTERESTED": 
                return 0.00; // 0% chance: Deal is lost
            default: 
                return 0.10; // Fallback
        }
    }
   
    // 7. P&L Report (Income minus Expenses)
    public java.util.Map<String, Double> generatePLReport() {
        java.util.List<com.example.crm.entity.Transaction> txs = transactionRepository.findAll();
        
        double totalIncome = txs.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .mapToDouble(t -> t.getBaseAmount() != null ? t.getBaseAmount() : 0.0).sum();
                
        double totalExpense = txs.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .mapToDouble(t -> t.getBaseAmount() != null ? t.getBaseAmount() : 0.0).sum();
        
        java.util.Map<String, Double> report = new java.util.HashMap<>();
        report.put("TotalIncome", totalIncome);
        report.put("TotalExpense", totalExpense);
        report.put("NetProfit", totalIncome - totalExpense);
        return report;
    }

    // 8. VAT Report (VAT Collected vs VAT Paid)
    public java.util.Map<String, Double> generateVatReport() {
        java.util.List<com.example.crm.entity.Transaction> txs = transactionRepository.findAll();
        
        double vatCollected = txs.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .mapToDouble(t -> t.getVatAmount() != null ? t.getVatAmount() : 0.0).sum();
                
        double vatPaid = txs.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .mapToDouble(t -> t.getVatAmount() != null ? t.getVatAmount() : 0.0).sum();
        
        java.util.Map<String, Double> report = new java.util.HashMap<>();
        report.put("VatCollected", vatCollected);
        report.put("VatPaid", vatPaid);
        report.put("VatPayable", vatCollected - vatPaid); // The amount owed to the government
        return report;
    }

    // 9. Zakat Report (Simplified 2.5% of Net Profit)
    public java.util.Map<String, Double> generateZakatReport() {
        double netProfit = generatePLReport().get("NetProfit");
        double zakatBase = Math.max(netProfit, 0.0); // Can't pay Zakat on a loss
        
        java.util.Map<String, Double> report = new java.util.HashMap<>();
        report.put("ZakatBase", zakatBase);
        report.put("ZakatDue", zakatBase * 0.025); // Standard 2.5%
        return report;
    }
}