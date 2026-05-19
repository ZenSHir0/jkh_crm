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
import rccl.diploma.crm.services.EmailService;
import rccl.diploma.crm.services.VerificationTokenService;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          VerificationTokenService verificationTokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
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
        if (user.getPhone() != null && user.getPhone().isBlank()) user.setPhone(null);
        if (user.getApartment() != null && user.getApartment().isBlank()) user.setApartment(null);
        user = userRepository.save(user);


        String token = verificationTokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user, token);

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

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        if (isAuthenticated()) return "redirect:/home";
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = verificationTokenService.createPasswordResetToken(user);
            emailService.sendPasswordReset(user, token);
        }
        return "redirect:/forgot-password?sent";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        User user = verificationTokenService.validatePasswordResetToken(token);
        if (user == null) return "redirect:/login?error=token-expired";
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String password,
                                @RequestParam String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return "redirect:/reset-password?token=" + token + "&error=mismatch";
        }
        User user = verificationTokenService.consumePasswordResetToken(token);
        if (user == null) return "redirect:/login?error=token-expired";
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return "redirect:/login?passwordreset";
    }

    @GetMapping("/home")
    public String afterLogin() {
        return "home";
    }
}
