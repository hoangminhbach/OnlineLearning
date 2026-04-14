package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model, Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            model.addAttribute("currentUser", user);
        }

        long totalUsers = userRepository.count();
        model.addAttribute("totalUsers", totalUsers);

        return "admin/dashboard";
    }
}
