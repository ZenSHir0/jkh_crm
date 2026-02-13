package rccl.diploma.crm.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.entity.enums.RequestType;
import rccl.diploma.crm.repository.RequestRepository;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/requests")
public class AdminRequestController {

    private final RequestRepository requestRepository;

    public AdminRequestController (RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @GetMapping
    public String listRequests(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) RequestStatus status,
                               @RequestParam(required = false) RequestType type,
                               @RequestParam(required = false) LocalDateTime startDate,
                               @RequestParam(required = false) LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Request> requests;
        if (status != null && type != null) {
            requests = requestRepository.findByStatusAndType(status, type, pageable);
        } else if (status != null) {
            requests = requestRepository.findByStatus(status, pageable);
        } else if (type != null) {
            requests = requestRepository.findByType(type, pageable);
        } else if (startDate != null && endDate != null) {
            requests = requestRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        } else {
            requests = requestRepository.findAll(pageable);
        }

        model.addAttribute("requests", requests.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());
        model.addAttribute("statuses", RequestStatus.values());
        model.addAttribute("types", RequestType.values());

        return "admin/requests";
    }
//
//    @GetMapping("/{id}")
//    public String requestDetails(){}
}
