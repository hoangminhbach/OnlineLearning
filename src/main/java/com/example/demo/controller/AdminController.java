package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model, Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            model.addAttribute("currentUser", user);
        }
        model.addAttribute("totalUsers", userService.count());
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            Model model) {

        Sort sortObj = Sort.by(Sort.Direction.DESC, "id");
        if (sort != null) {
            sortObj = Sort.by(Sort.Direction.ASC, sort);
        }

        Page<User> users = userService.findAll(PageRequest.of(page, size, sortObj));
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());

        return "admin/userList";
    }
}
