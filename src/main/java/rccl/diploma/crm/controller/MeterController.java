package rccl.diploma.crm.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rccl.diploma.crm.dto.MeterSubmitDTO;
import rccl.diploma.crm.entity.MeterReading;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.MeterType;
import rccl.diploma.crm.repository.UserRepository;
import rccl.diploma.crm.services.MeterService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/meters")
public class MeterController {

    private final UserRepository userRepository;
    private final MeterService meterService;

    public MeterController(UserRepository userRepository, MeterService meterService) {
        this.userRepository = userRepository;
        this.meterService = meterService;
    }

    @GetMapping("/submit")
    public String showForm(Model model, Authentication authentication) {
        User user = getUser(authentication);

        LocalDate currentPeriod = LocalDate.now().withDayOfMonth(1);
        String raw = currentPeriod.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("ru")));
        String periodDisplay = Character.toUpperCase(raw.charAt(0)) + raw.substring(1);

        // Типы, уже переданные за текущий период
        List<MeterReading> thisMonth = meterService.getReadingsForPeriod(user, currentPeriod);
        Set<MeterType> submittedTypes = thisMonth.stream()
                .map(MeterReading::getMeterType)
                .collect(Collectors.toSet());

        // Предыдущие показания для подсказки в полях
        Map<MeterType, BigDecimal> lastValues = meterService.getLastValues(user);

        // История
        List<MeterReading> history = meterService.getReadingsForUser(user);

        model.addAttribute("dto", new MeterSubmitDTO());
        model.addAttribute("meterTypes", MeterType.values());
        model.addAttribute("submittedTypes", submittedTypes);
        model.addAttribute("lastValues", lastValues);
        model.addAttribute("history", history);
        model.addAttribute("currentPeriod", currentPeriod);
        model.addAttribute("periodDisplay", periodDisplay);
        model.addAttribute("user", user);

        return "meters/submit";
    }

    @PostMapping("/submit")
    public String submit(@ModelAttribute MeterSubmitDTO dto,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        User user = getUser(authentication);
        LocalDate period = LocalDate.now().withDayOfMonth(1);

        try {
            int count = meterService.submitReadings(dto, user, period);
            redirectAttributes.addFlashAttribute("success",
                    "Показания переданы по " + count + " счётчик" + plural(count));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось сохранить показания: " + e.getMessage());
        }
        return "redirect:/meters/submit";
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    /** Простая русская форма для «счётчик/счётчика/счётчиков». */
    private String plural(int n) {
        if (n % 10 == 1 && n % 100 != 11) return "у";
        if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) return "а";
        return "ов";
    }
}
