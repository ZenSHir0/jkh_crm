package rccl.diploma.crm.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rccl.diploma.crm.dto.RequestDTO;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.RequestPhoto;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final FileStorageService fileStorageService;

    public RequestService(RequestRepository requestRepository, FileStorageService fileStorageService) {
        this.requestRepository = requestRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public void createRequestFromDTO(RequestDTO requestDTO, User resident) {

        Request request = Request.builder()
                .resident(resident)
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .type(requestDTO.getType())
                .status(RequestStatus.NEW)
                .createdAt(LocalDateTime.now())
                .deadline(LocalDateTime.now().plusDays(1))
                .build();

        Request saved = requestRepository.save(request);

        MultipartFile[] files = requestDTO.getPhotos();
        if (files != null && files.length > 0) {
            String subDir = "requests/" + saved.getId();

            for (MultipartFile file : files) {
                String relativePath = fileStorageService.saveFile(file, subDir);
                if (relativePath != null) {

                    RequestPhoto photo = RequestPhoto.builder()
                            .request(saved)
                            .filePath(relativePath)
                            .originalFileName(file.getOriginalFilename())
                            .fileSize(file.getSize())
                            .contentType(file.getContentType())
                            .uploadedBy(resident)
                            .uploadedAt(LocalDateTime.now())
                            .build();

                    saved.addPhoto(photo);
                }
            }
            requestRepository.save(saved);
        }
    }

    public Page<Request> getRequestsByResident(User resident, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return requestRepository.findByResident(resident, pageable);
    }

    public List<Request> getLastRequestsByResident(User resident) {
        return requestRepository.findTop10ByResidentOrderByCreatedAtDesc(resident);
    }
}

