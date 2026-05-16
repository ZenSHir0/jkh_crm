package rccl.diploma.crm.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rccl.diploma.crm.entity.enums.Role;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.repository.UserRepository;
import rccl.diploma.crm.services.VerificationTokenService;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    @GetMapping("/")
    public String toHome() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login() {
        if (isAuthenticated()) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (isAuthenticated()) {
            return "redirect:/home";
        }
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "redirect:/register?error=username";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.RESIDENT);
        user.setEnabled(false);
        user = userRepository.save(user);

        verificationTokenService.sendVerificationToken(user);

        return "redirect:/register?verificationsent";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        if (verificationTokenService.verifyToken(token)) {
            return "redirect:/login?verified=success";
        } else {
            return "redirect:/login?error=token-expired";
        }
    }

    @GetMapping("/home")
    public String afterLogin() {
        return "home";
    }
}
