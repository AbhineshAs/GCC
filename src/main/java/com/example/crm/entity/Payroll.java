package com.example.crm.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "payroll")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer month;
    private Integer year;

    private Double basicSalary;
    private Double totalAllowances;
    private Double gosiDeduction;
    private Double lopDeduction; 
    private Double otherDeductions;
    private Double netSalary;
    private String status; 
    private LocalDateTime processedAt;
    
    

    // IMPORTANT: Generate Getters and Setters for all fields above.
    // Especially: getMonth(), getYear(), setMonth(), setYear()
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }
    public Double getGosiDeduction() { return gosiDeduction; }
    public void setGosiDeduction(Double gosiDeduction) { this.gosiDeduction = gosiDeduction; }
    public Double getLopDeduction() { return lopDeduction; }
    public void setLopDeduction(Double lopDeduction) { this.lopDeduction = lopDeduction; }
    public Double getNetSalary() { return netSalary; }
    public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
 // SETTER FOR THE ERROR IN image_b1d6ef.jpg
    public Double getTotalAllowances() { return totalAllowances; }
    public void setTotalAllowances(Double totalAllowances) { this.totalAllowances = totalAllowances; }

    // SETTER FOR THE ERROR IN image_b1d70d.jpg
    public Double getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(Double otherDeductions) { this.otherDeductions = otherDeductions; }
}