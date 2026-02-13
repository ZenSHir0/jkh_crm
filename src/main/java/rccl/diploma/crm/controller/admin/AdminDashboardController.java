package rccl.diploma.crm.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rccl.diploma.crm.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final UserService userService;

    public AdminDashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/dashboard";
    }
}
