package rccl.diploma.crm.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rccl.diploma.crm.repository.UserRepository;
import rccl.diploma.crm.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AdminDashboardController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("users", (search != null && !search.isBlank())
                ? userRepository.search(search)
                : userService.getAllUsers());
        model.addAttribute("search", search);
        return "admin/dashboard";
    }
}
