package rccl.diploma.crm.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rccl.diploma.crm.entity.Building;
import rccl.diploma.crm.repository.BuildingRepository;

@Controller
@RequestMapping("/admin/buildings")
public class AdminBuildingController {

    private final BuildingRepository buildingRepository;

    public AdminBuildingController(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("buildings", buildingRepository.findAll());
        return "admin/buildings";
    }

    @PostMapping
    public String create(@RequestParam String address, RedirectAttributes redirectAttributes) {
        if (address == null || address.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Адрес не может быть пустым");
            return "redirect:/admin/buildings";
        }
        buildingRepository.save(Building.builder().address(address.strip()).build());
        redirectAttributes.addFlashAttribute("success", "Дом добавлен");
        return "redirect:/admin/buildings";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        buildingRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Дом удалён");
        return "redirect:/admin/buildings";
    }
}
