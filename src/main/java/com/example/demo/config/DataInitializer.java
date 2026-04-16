package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create roles if not exist
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            UserRole adminRole = new UserRole();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator");
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName("EXPERT").isEmpty()) {
            UserRole expertRole = new UserRole();
            expertRole.setName("EXPERT");
            expertRole.setDescription("Expert");
            roleRepository.save(expertRole);
        }

        if (roleRepository.findByName("LEARNER").isEmpty()) {
            UserRole learnerRole = new UserRole();
            learnerRole.setName("LEARNER");
            learnerRole.setDescription("Learner");
            roleRepository.save(learnerRole);
        }

        // Create admin user if not exist
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator");
            admin.setEnabled(true);
            admin.setRole(roleRepository.findByName("ADMIN").get());
            userRepository.save(admin);
            System.out.println("Admin user created: admin@gmail.com / admin123");
        }
    }
}
