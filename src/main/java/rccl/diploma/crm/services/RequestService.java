package rccl.diploma.crm.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
    public Request createRequest(Request request, User resident, MultipartFile[] files) {

        request.setResident(resident);
        request.setStatus(RequestStatus.NEW);
        request.setCreatedAt(LocalDateTime.now());

        Request saved = requestRepository.save(request);

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        String uploadDir = "uploads/requests/" + saved.getId() + "/";
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
                                .build();

                        //saved.addPhoto(photo);
                    } catch (IOException e) {
                        throw new RuntimeException("Ошибка загрузки фото", e);
                    }

                }
            }
            requestRepository.save(saved);
        }
        return saved;
    }
}
