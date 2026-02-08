package rccl.diploma.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rccl.diploma.crm.entity.enums.Role;
import rccl.diploma.crm.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam Role role) {
        userService.changeRole(id, role);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/enabled")
    public String toggleEnabled(@PathVariable Long id) {
        userService.toggleEnabled(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }
}
