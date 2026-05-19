package rccl.diploma.crm.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.entity.enums.RequestType;
import rccl.diploma.crm.entity.enums.Role;
import rccl.diploma.crm.repository.RequestRepository;
import rccl.diploma.crm.repository.UserRepository;
import rccl.diploma.crm.services.RequestService;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/requests")
public class AdminRequestController {

    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final UserRepository userRepository;

    public AdminRequestController(RequestRepository requestRepository,
                                  RequestService requestService,
                                  UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.requestService = requestService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listRequests(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) RequestStatus status,
                               @RequestParam(required = false) RequestType type,
                               @RequestParam(required = false) LocalDate startDate,
                               @RequestParam(required = false) LocalDate endDate) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Request> spec = (root, query, cb) -> cb.conjunction();
        if (status != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (type != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("type"), type));
        }
        if (startDate != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay()));
        }
        if (endDate != null) {
            spec = spec.and((root, q, cb) -> cb.lessThan(root.get("createdAt"), endDate.plusDays(1).atStartOfDay()));
        }

        Page<Request> requests = requestRepository.findAll(spec, pageable);

        model.addAttribute("requests", requests.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());
        model.addAttribute("statuses", RequestStatus.values());
        model.addAttribute("types", RequestType.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", type);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/requests";
    }

    @GetMapping("/{id}")
    public String requestDetails(@PathVariable Long id, Model model) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));

        model.addAttribute("request", request);
        model.addAttribute("photos", request.getPhotos());
        model.addAttribute("comments", request.getComments());
        model.addAttribute("masters", userRepository.findAllByRole(Role.MASTER));

        return "admin/request-details";
    }

    private User getAdmin(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @PostMapping("/{id}/assign")
    public String assignMaster(@PathVariable Long id,
                               @RequestParam Long masterId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            requestService.assignMaster(id, masterId, getAdmin(authentication));
            redirectAttributes.addFlashAttribute("success", "Мастер назначен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/requests/" + id;
    }

    @PostMapping("/{id}/accept")
    public String acceptRequest(@PathVariable Long id, Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            requestService.acceptRequest(id, getAdmin(authentication), "Администратор взял заявку в работу");
            redirectAttributes.addFlashAttribute("success", "Заявка взята в работу");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/requests/" + id;
    }

    @PostMapping("/{id}/complete")
    public String completeRequest(@PathVariable Long id, Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            requestService.completeRequest(id, getAdmin(authentication), "Администратор закрыл заявку");
            redirectAttributes.addFlashAttribute("success", "Заявка закрыта");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/requests/" + id;
    }

    @PostMapping("/{id}/reopen")
    public String reopenRequest(@PathVariable Long id, Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            requestService.reopenRequest(id, getAdmin(authentication), "Администратор возобновил заявку");
            redirectAttributes.addFlashAttribute("success", "Заявка возобновлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/requests/" + id;
    }

    @PostMapping("/{id}/reject")
    public String rejectRequest(@PathVariable Long id,
                                @RequestParam String reason,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            requestService.rejectRequest(id, getAdmin(authentication), reason, "Администратор отклонил заявку. Причина");
            redirectAttributes.addFlashAttribute("success", "Заявка отклонена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/requests/" + id;
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam String text,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            requestService.addComment(id, getAdmin(authentication), text);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/requests/" + id;
    }
}
