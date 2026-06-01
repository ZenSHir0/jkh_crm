package rccl.diploma.crm.controller.master;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.repository.BalanceTransactionRepository;
import rccl.diploma.crm.repository.UserRepository;

@Controller
@RequestMapping("/master")
class MasterController {
    UserRepository userRepository;
    BalanceTransactionRepository transactionRepository;

    public MasterController(UserRepository userRepository, BalanceTransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/balance")
    public String balance(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        model.addAttribute("master", user);
        model.addAttribute("transactions", transactionRepository.findByMasterOrderByCreatedAtDesc(user));
        return "master/balance";
    }

}
