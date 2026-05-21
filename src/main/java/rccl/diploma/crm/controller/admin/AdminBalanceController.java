package rccl.diploma.crm.controller.admin;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rccl.diploma.crm.entity.BalanceTransaction;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.TransactionType;
import rccl.diploma.crm.repository.BalanceTransactionRepository;
import rccl.diploma.crm.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/masters")
public class AdminBalanceController {

    private final UserRepository userRepository;
    private final BalanceTransactionRepository transactionRepository;

    public AdminBalanceController(UserRepository userRepository,
                                  BalanceTransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/{id}/balance")
    public String masterBalance(@PathVariable Long id, Model model) {
        User master = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        model.addAttribute("master", master);
        model.addAttribute("transactions", transactionRepository.findByMasterOrderByCreatedAtDesc(master));
        return "admin/master-balance";
    }

    @PostMapping("/{id}/fine")
    public String addFine(@PathVariable Long id,
                          @RequestParam BigDecimal amount,
                          @RequestParam String comment,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        User master = userRepository.findById(id).orElseThrow();
        User admin  = userRepository.findByUsername(authentication.getName()).orElseThrow();
        master.setBalance(master.getBalance().subtract(amount));
        userRepository.save(master);
        transactionRepository.save(BalanceTransaction.builder()
                .master(master).createdBy(admin)
                .type(TransactionType.FINE)
                .amount(amount).comment(comment)
                .createdAt(LocalDateTime.now()).build());
        redirectAttributes.addFlashAttribute("success", "Штраф начислен");
        return "redirect:/admin/masters/" + id + "/balance";
    }

    @PostMapping("/{id}/bonus")
    public String addBonus(@PathVariable Long id,
                           @RequestParam BigDecimal amount,
                           @RequestParam String comment,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        User master = userRepository.findById(id).orElseThrow();
        User admin  = userRepository.findByUsername(authentication.getName()).orElseThrow();
        master.setBalance(master.getBalance().add(amount));
        userRepository.save(master);
        transactionRepository.save(BalanceTransaction.builder()
                .master(master).createdBy(admin)
                .type(TransactionType.BONUS)
                .amount(amount).comment(comment)
                .createdAt(LocalDateTime.now()).build());
        redirectAttributes.addFlashAttribute("success", "Бонус начислен");
        return "redirect:/admin/masters/" + id + "/balance";
    }

    @PostMapping("/{id}/payout")
    public String payout(@PathVariable Long id,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        User master = userRepository.findById(id).orElseThrow();
        User admin  = userRepository.findByUsername(authentication.getName()).orElseThrow();
        BigDecimal amount = master.getBalance();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("error", "Баланс мастера равен нулю");
            return "redirect:/admin/masters/" + id + "/balance";
        }
        transactionRepository.save(BalanceTransaction.builder()
                .master(master).createdBy(admin)
                .type(TransactionType.PAYOUT)
                .amount(amount)
                .comment("Выплата за период")
                .createdAt(LocalDateTime.now()).build());
        master.setBalance(BigDecimal.ZERO);
        userRepository.save(master);
        redirectAttributes.addFlashAttribute("success", "Выплата " + amount + " ₽ проведена");
        return "redirect:/admin/masters/" + id + "/balance";
    }
}
