package rccl.diploma.crm.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rccl.diploma.crm.dto.RequestDTO;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.RequestType;
import rccl.diploma.crm.repository.UserRepository;
import rccl.diploma.crm.services.RequestService;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final UserRepository userRepository;
    private final RequestService requestService;

    public RequestController(UserRepository userRepository, RequestService requestService) {
        this.userRepository = userRepository;
        this.requestService = requestService;
    }

    @GetMapping("/new")
    public String showNewRequestForm(Model model) {
        model.addAttribute("request", new RequestDTO());
        model.addAttribute("types", RequestType.values());
        return "requests/new";
    }

    @PostMapping("/new")
    public String createNewRequest(@Valid @ModelAttribute("request") RequestDTO requestDTO,
                                BindingResult result,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getAllErrors());
            redirectAttributes.addFlashAttribute("request", requestDTO);
            return "redirect:/requests/new";
        }

        String username = authentication.getName();
        User resident = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        try {
            requestService.createRequestFromDTO(requestDTO, resident);
            redirectAttributes.addFlashAttribute("success", "Заявка успешно создана");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось создать заявку: " + e.getMessage());
            return "redirect:/requests/new";
        }
    }

    @GetMapping("/{id}")
    public String requestDetails(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Request request = requestService.getRequestByIdForUser(id, currentUser);

        model.addAttribute("request", request);
        model.addAttribute("photos", request.getPhotos());
        model.addAttribute("comments", request.getComments());

        return "requests/details";
    }

    @GetMapping("/my")
    public String myRequests(Model model, Authentication authentication,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {

        String username = authentication.getName();
        User resident = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Page<Request> requests = requestService.getRequestsForUser(resident, page, size);
        model.addAttribute("requests", requests.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());

        return "requests/my";
    }
}
