package rccl.diploma.crm.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public String dashboard(@RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0")  int page,
                            @RequestParam(defaultValue = "20") int size,
                            Model model) {

        var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<?> users = (search != null && !search.isBlank())
                ? userRepository.searchPage(search, pageable)
                : userService.getAllUsers(pageable);

        int totalPages = users.getTotalPages();
        model.addAttribute("users",       users.getContent());
        model.addAttribute("search",      search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  totalPages);
        model.addAttribute("pageStart",   Math.max(0, page - 2));
        model.addAttribute("pageEnd",     Math.min(totalPages - 1, page + 2));
        return "admin/dashboard";
    }
}
