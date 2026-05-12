package rccl.diploma.crm.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rccl.diploma.crm.dto.RequestDTO;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.repository.RequestRepository;
import rccl.diploma.crm.repository.UserRepository;
import rccl.diploma.crm.services.RequestService;
import rccl.diploma.crm.services.UserService;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final RequestService requestService;

    public ProfileController(UserRepository userRepository, UserService userService, RequestRepository requestRepository, RequestService requestService){
        this.userRepository = userRepository;
        this.userService = userService;
        this.requestService = requestService;
    }

    @GetMapping
    public String profile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        List<Request> user_requests = requestService.getLastRequestsByResident(user);

        model.addAttribute("requests", user_requests);
        model.addAttribute("user", user);

        return "profile/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("user") User user,
                                BindingResult result,
                                Model model) {

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors", errors);
            return "profile/profile";
        }
        userService.updateUser(user);
        return "redirect:/profile";
    }
}
