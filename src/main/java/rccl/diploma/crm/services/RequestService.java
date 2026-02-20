package rccl.diploma.crm.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rccl.diploma.crm.dto.RequestDTO;
import rccl.diploma.crm.entity.Request;
import rccl.diploma.crm.entity.RequestPhoto;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.repository.RequestRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
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
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        String uploadDir = System.getenv("UPLOADS_DIR") + "/requests/" + saved.getId() + "/";
                        Path uploadPath = Paths.get(uploadDir);
                        Files.createDirectories(uploadPath);
                        String fullPath = uploadDir + fileName;
                        file.transferTo(Paths.get(fullPath));

                        RequestPhoto photo = RequestPhoto.builder()
                                .request(saved)
                                .filePath("/" + fullPath)
                                .originalFileName(file.getOriginalFilename())
                                .fileSize(file.getSize())
                                .contentType(file.getContentType())
                                .uploadedBy(resident)
                                .uploadedAt(LocalDateTime.now())
                                .build();

                        saved.addPhoto(photo);
                    } catch (IOException e) {
                        throw new RuntimeException("Ошибка загрузки фото", e);
                    }

                }
            }
            requestRepository.save(saved);
        }
    }
}
