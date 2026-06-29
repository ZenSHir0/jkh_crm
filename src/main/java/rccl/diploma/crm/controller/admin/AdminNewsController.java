package rccl.diploma.crm.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rccl.diploma.crm.services.NewsService;

@Controller
@RequestMapping("/admin")
public class AdminNewsController {

    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public String getNewsPage(Model model) {
        model.addAttribute("news", newsService.getAllNews());
        return "admin/news";
    }
}
