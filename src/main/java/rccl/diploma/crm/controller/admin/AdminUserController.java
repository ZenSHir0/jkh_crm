package rccl.diploma.crm.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rccl.diploma.crm.entity.enums.Role;
import rccl.diploma.crm.services.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam Role role) {
        userService.changeRole(id, role);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/{id}/enabled")
    public String toggleEnabled(@PathVariable Long id) {
        userService.toggleEnabled(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }

}
