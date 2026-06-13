package rccl.diploma.crm.services;

import org.springframework.stereotype.Service;
import rccl.diploma.crm.dto.AdminHomeStats;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.repository.BuildingRepository;
import rccl.diploma.crm.repository.RequestRepository;
import rccl.diploma.crm.repository.UserRepository;

@Service
public class StatsService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    //private final NewsRepository newsRepository;

    public StatsService(RequestRepository requestRepository, UserRepository userRepository, BuildingRepository buildingRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
    }

    public AdminHomeStats getStats() {

        return new AdminHomeStats(
                requestRepository.count(),
                requestRepository.countByStatus(RequestStatus.IN_PROGRESS),
                requestRepository.countByStatus(RequestStatus.NEW),
                requestRepository.countByStatus(RequestStatus.REJECTED),
                userRepository.count(),
                buildingRepository.count(),
                10
        );
    }
}
