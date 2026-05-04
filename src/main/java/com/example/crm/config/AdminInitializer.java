package com.example.crm.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.crm.entity.User;
import com.example.crm.repository.UserRepository;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository repo) {
        return args -> {

            if (repo.findByEmail("admin@gmail.com") == null) {

                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@gmail.com");

                // password = admin123 (bcrypt encoded)
                admin.setPassword("admin@123");

                admin.setRole("ADMIN");

                repo.save(admin);

                System.out.println("✅ Admin user created!");
            } else {
                System.out.println("⚡ Admin already exists");
            }
        };
    }
}