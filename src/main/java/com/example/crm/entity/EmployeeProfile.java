package com.example.crm.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "employee_profiles")
public class EmployeeProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
 public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Double getAnnualLeaveBalance() {
		return annualLeaveBalance;
	}

	public void setAnnualLeaveBalance(Double annualLeaveBalance) {
		this.annualLeaveBalance = annualLeaveBalance;
	}

	public Double getSickLeaveBalance() {
		return sickLeaveBalance;
	}

	public void setSickLeaveBalance(Double sickLeaveBalance) {
		this.sickLeaveBalance = sickLeaveBalance;
	}

	public Double getEmergencyLeaveBalance() {
		return emergencyLeaveBalance;
	}

	public void setEmergencyLeaveBalance(Double emergencyLeaveBalance) {
		this.emergencyLeaveBalance = emergencyLeaveBalance;
	}

	public Double getTotalLeaveTaken() {
		return totalLeaveTaken;
	}

	public void setTotalLeaveTaken(Double totalLeaveTaken) {
		this.totalLeaveTaken = totalLeaveTaken;
	}

	public Double getHousingAllowance() {
		return housingAllowance;
	}

	public void setHousingAllowance(Double housingAllowance) {
		this.housingAllowance = housingAllowance;
	}

	public Double getTransportationAllowance() {
		return transportationAllowance;
	}

	public void setTransportationAllowance(Double transportationAllowance) {
		this.transportationAllowance = transportationAllowance;
	}

	public Double getOtherAllowances() {
		return otherAllowances;
	}

	public void setOtherAllowances(Double otherAllowances) {
		this.otherAllowances = otherAllowances;
	}

	public Double getGosiEmployeeContribution() {
		return gosiEmployeeContribution;
	}

	public void setGosiEmployeeContribution(Double gosiEmployeeContribution) {
		this.gosiEmployeeContribution = gosiEmployeeContribution;
	}

	public Double getBasicSalary() {
		return basicSalary;
	}

	public void setBasicSalary(Double basicSalary) {
		this.basicSalary = basicSalary;
	}

	public String getGosiNumber() {
		return gosiNumber;
	}

	public void setGosiNumber(String gosiNumber) {
		this.gosiNumber = gosiNumber;
	}

	public String getIbanNumber() {
		return ibanNumber;
	}

	public void setIbanNumber(String ibanNumber) {
		this.ibanNumber = ibanNumber;
	}

	// --- LEAVE BALANCE FIELDS ---
    private Double annualLeaveBalance = 21.0;
    private Double sickLeaveBalance = 15.0;
    private Double emergencyLeaveBalance = 5.0;
    private Double totalLeaveTaken = 0.0;
    
 // Inside EmployeeProfile.java
    private Double housingAllowance = 0.0;
    private Double transportationAllowance = 0.0;
    private Double otherAllowances = 0.0;
    private Double gosiEmployeeContribution = 0.0;
 

   /** private Double basicSalary;
    private Double housingAllowance;
    private Double transportationAllowance;
    private String ibanNumber; // Required for WPS
    private String gosiNumber; // Required for KSA Compliance**/
    // Total Salary Calculation
    public Double getTotalGrossSalary() {
        return basicSalary + housingAllowance + transportationAllowance;
        
    }
    
 // Earnings
    private Double basicSalary;
   

    // Statutory / Fixed Deductions
   
    private String gosiNumber;
    private String ibanNumber; // Required for WPS (Wage Protection System)
    
 // Inside EmployeeProfile.java
    private Double lopDeduction = 0.0;

    // Add Getter and Setter
    public Double getLopDeduction() {
        return lopDeduction;
    }

    public void setLopDeduction(Double lopDeduction) {
        this.lopDeduction = lopDeduction;
    }
}