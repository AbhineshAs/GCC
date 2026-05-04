package com.example.crm.contoller;

import com.example.crm.entity.Lead;
import com.example.crm.entity.Task;
import com.example.crm.entity.User;
import com.example.crm.repository.LeadRepository;
import com.example.crm.repository.TaskRepository;
import com.example.crm.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crm") // Base URL for Postman
public class TaskApiController {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;

    // 1. GET all Leads (Testing in Postman)
    @GetMapping("/leads")
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    // 2. GET all Tasks (Testing in Postman)
    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // 3. Simple Test Endpoint to check connection
    @GetMapping("/status")
    public String checkStatus() {
        return "CRM API is Online and Connected to Postman";
    }
    
 // 4. Create a new Task from Postman (Body -> raw -> JSON)
    @PostMapping("/tasks/add")
    public Task addNewTask(@RequestBody Task newTask) {
        return taskRepository.save(newTask);
    }
    
 // 5. GET Executives by Manager ID (For the Lead Report dropdown)
    @GetMapping("/managers/{id}/executives")
    public List<User> getExecutivesByManager(@PathVariable Long id) {
        return userRepository.findAll().stream()
                .filter(u -> u.getManager() != null && u.getManager().getId().equals(id))
                .toList();
    }
}