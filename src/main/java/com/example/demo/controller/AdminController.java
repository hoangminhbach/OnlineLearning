package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.model.CourseCategory;
import com.example.demo.model.Order;
import com.example.demo.model.Slider;
import com.example.demo.model.dto.OrderFilter;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.enums.SliderStatus;
import com.example.demo.service.UserService;
import com.example.demo.service.CourseCategoryService;
import com.example.demo.service.OrderService;
import com.example.demo.service.SliderService;
import com.example.demo.service.UploadService;
import com.example.demo.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final CourseCategoryService categoryService;
    private final OrderService orderService;
    private final SliderService sliderService;
    private final UploadService uploadService;

    public AdminController(UserService userService, RoleRepository roleRepository,
                          CourseCategoryService categoryService, OrderService orderService,
                          SliderService sliderService, UploadService uploadService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.sliderService = sliderService;
        this.uploadService = uploadService;
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

    @GetMapping("/admin/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        List<UserRole> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        return "admin/createUser";
    }

    @PostMapping("/admin/users/create")
    public String createUser(@ModelAttribute User user, RedirectAttributes ra) {
        try {
            userService.save(user);
            ra.addFlashAttribute("success", "Tạo người dùng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Email đã tồn tại!");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/update/{id}")
    public String updateUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        List<UserRole> roles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "admin/updateUser";
    }

    @PostMapping("/admin/users/update")
    public String updateUser(@ModelAttribute User user, RedirectAttributes ra) {
        userService.update(user);
        ra.addFlashAttribute("success", "Cập nhật người dùng thành công!");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteById(id);
        ra.addFlashAttribute("success", "Xóa người dùng thành công!");
        return "redirect:/admin/users";
    }

    // ==================== COURSE CATEGORY ====================
    @GetMapping("/admin/course_categories")
    public String getCourseCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            Model model) {

        Page<CourseCategory> categories = categoryService.findAll(PageRequest.of(page, size));
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        return "admin/courseCategory/list";
    }

    @GetMapping("/admin/course_categories/create")
    public String createCategoryForm(Model model) {
        model.addAttribute("category", new CourseCategory());
        return "admin/courseCategory/create";
    }

    @PostMapping("/admin/course_categories/create")
    public String createCategory(@ModelAttribute CourseCategory category, RedirectAttributes ra) {
        categoryService.save(category);
        ra.addFlashAttribute("success", "Tạo danh mục thành công!");
        return "redirect:/admin/course_categories";
    }

    @GetMapping("/admin/course_categories/update/{id}")
    public String updateCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "admin/courseCategory/update";
    }

    @PostMapping("/admin/course_categories/update")
    public String updateCategory(@ModelAttribute CourseCategory category, RedirectAttributes ra) {
        categoryService.update(category);
        ra.addFlashAttribute("success", "Cập nhật danh mục thành công!");
        return "redirect:/admin/course_categories";
    }

    @GetMapping("/admin/course_categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        categoryService.deleteById(id);
        ra.addFlashAttribute("success", "Xóa danh mục thành công!");
        return "redirect:/admin/course_categories";
    }

    // ==================== ORDER MANAGEMENT ====================
    @GetMapping("/admin/orders")
    public String orders(@ModelAttribute OrderFilter filter,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {
        Page<Order> orders = orderService.findWithFilters(
            filter.getKeyword(), filter.getStatus(),
            filter.getStartDate(), filter.getEndDate(),
            PageRequest.of(page, size, Sort.by("orderDate").descending())
        );
        model.addAttribute("orders", orders);
        model.addAttribute("filter", filter);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        return "admin/orderDashboard";
    }

    @GetMapping("/admin/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        orderService.findById(id).ifPresent(order -> model.addAttribute("order", order));
        return "admin/orderDetail";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes ra) {
        orderService.updateOrderStatus(id, status);
        ra.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        return "redirect:/admin/orders";
    }

    // ==================== SLIDER MANAGEMENT ====================
    @GetMapping("/admin/sliders")
    public String sliders(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) SliderStatus status,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Page<Slider> sliders = sliderService.findWithFilters(keyword, status,
            PageRequest.of(page, size, Sort.by("orderNumber").ascending()));
        model.addAttribute("sliders", sliders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/slider/list";
    }

    @GetMapping("/admin/sliders/create")
    public String createSliderForm(Model model) {
        model.addAttribute("slider", new Slider());
        model.addAttribute("statuses", SliderStatus.values());
        return "admin/slider/create";
    }

    @PostMapping("/admin/sliders/create")
    public String createSlider(@ModelAttribute Slider slider,
                              @RequestParam(required = false) MultipartFile imageFile,
                              RedirectAttributes ra, Model model) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = uploadService.uploadSlider(imageFile);
                slider.setImageUrl(imageUrl);
            }
            sliderService.createSlider(slider);
            ra.addFlashAttribute("success", "Tạo slider thành công!");
            return "redirect:/admin/sliders";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("statuses", SliderStatus.values());
            return "admin/slider/create";
        }
    }

    @GetMapping("/admin/sliders/update/{id}")
    public String updateSliderForm(@PathVariable Long id, Model model) {
        sliderService.findById(id).ifPresent(slider -> model.addAttribute("slider", slider));
        model.addAttribute("statuses", SliderStatus.values());
        return "admin/slider/update";
    }

    @PostMapping("/admin/sliders/update/{id}")
    public String updateSlider(@PathVariable Long id,
                              @ModelAttribute Slider slider,
                              @RequestParam(required = false) MultipartFile imageFile,
                              RedirectAttributes ra, Model model) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = uploadService.uploadSlider(imageFile);
                slider.setImageUrl(imageUrl);
            }
            sliderService.updateSlider(id, slider);
            ra.addFlashAttribute("success", "Cập nhật slider thành công!");
            return "redirect:/admin/sliders";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("statuses", SliderStatus.values());
            return "admin/slider/update";
        }
    }

    @PostMapping("/admin/sliders/delete/{id}")
    public String deleteSlider(@PathVariable Long id, RedirectAttributes ra) {
        sliderService.deleteSlider(id);
        ra.addFlashAttribute("success", "Xóa slider thành công!");
        return "redirect:/admin/sliders";
    }

    @PostMapping("/admin/sliders/approve/{id}")
    public String approveSlider(@PathVariable Long id, RedirectAttributes ra) {
        sliderService.approveSlider(id);
        ra.addFlashAttribute("success", "Duyệt slider thành công!");
        return "redirect:/admin/sliders";
    }

    @PostMapping("/admin/sliders/reject/{id}")
    public String rejectSlider(@PathVariable Long id, RedirectAttributes ra) {
        sliderService.rejectSlider(id);
        ra.addFlashAttribute("success", "Từ chối slider thành công!");
        return "redirect:/admin/sliders";
    }
}
