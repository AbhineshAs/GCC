package com.example.crm.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    	
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Double getBaseSalary() {
		return baseSalary;
	}

	public void setBaseSalary(Double baseSalary) {
		this.baseSalary = baseSalary;
	}

	public User getManager() {
		return manager;
	}

	public void setManager(User manager) {
		this.manager = manager;
	}

	public List<User> getExecutives() {
		return executives;
	}

	public void setExecutives(List<User> executives) {
		this.executives = executives;
	}
	private String password;
    private String role; // Roles: ADMIN, MANAGER, EXECUTIVE [cite: 9]
    
    private Long createdBy;
    
    private Double baseSalary; // Fixed monthly salary

    // Hierarchy Link: If this user is an EXECUTIVE, this field stores their MANAGER
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;
    
 // Helper for template to see team size
    @OneToMany(mappedBy = "manager")
    private List<User> executives;
    
 // Inside User.java

    @Lob
    @Column(columnDefinition = "LONGTEXT") // Use LONGTEXT for MySQL to store large images
    private String profilePicture;

    // Getter
    public String getProfilePicture() {
        return profilePicture;
    }

    // Setter (This is the method your controller is looking for)
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
 // Inside User.java
    @jakarta.persistence.Transient
    private int subordinateCount;
    
    

    public int getSubordinateCount() { return subordinateCount; }
    public void setSubordinateCount(int subordinateCount) { this.subordinateCount = subordinateCount; }
}