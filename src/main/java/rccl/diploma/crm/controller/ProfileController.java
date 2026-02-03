package rccl.diploma.crm.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.repository.UserRepository;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    //private final ReauestRepository requestRepository;

    public ProfileController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping
    public String profile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        model.addAttribute("user", user);

        return "profile";
    }
}
