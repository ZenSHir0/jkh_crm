package rccl.diploma.crm.services;

import org.springframework.stereotype.Service;
import rccl.diploma.crm.entity.News;
import rccl.diploma.crm.repository.NewsRepository;

import java.util.List;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> getAllNews() {
        return newsRepository.findAllByOrderByValidUntilDesc();
    }
    public List<News> getRecentNews() { return newsRepository.findTop10ByIsValidTrueOrderByValidUntilDesc(); }

}
