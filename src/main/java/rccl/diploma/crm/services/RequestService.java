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
import rccl.diploma.crm.entity.RequestComment;
import rccl.diploma.crm.entity.RequestPhoto;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.enums.RequestStatus;
import rccl.diploma.crm.entity.enums.Role;
import rccl.diploma.crm.repository.RequestCommentRepository;
import rccl.diploma.crm.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestCommentRepository commentRepository;
    private final FileStorageService fileStorageService;

    public RequestService(RequestRepository requestRepository,
                          RequestCommentRepository commentRepository,
                          FileStorageService fileStorageService) {
        this.requestRepository = requestRepository;
        this.commentRepository = commentRepository;
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

    public Page<Request> getRequestsForUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (user.getRole() == Role.MASTER) {
            return requestRepository.findByMaster(user, pageable);
        }
        return requestRepository.findByResident(user, pageable);
    }

    public List<Request> getLastRequestsByResident(User resident) {
        return requestRepository.findTop10ByResidentOrderByCreatedAtDesc(resident);
    }

    @Transactional
    public void acceptRequest(Long id, User master) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        if (request.getStatus() != RequestStatus.NEW) {
            throw new RuntimeException("Заявку можно взять в работу только в статусе «Новая»");
        }
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setMaster(master);
        requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(Long id, User master, String reason) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        if (request.getStatus() != RequestStatus.NEW && request.getStatus() != RequestStatus.IN_PROGRESS) {
            throw new RuntimeException("Нельзя отклонить заявку в текущем статусе");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setClosedAt(LocalDateTime.now());
        requestRepository.save(request);

        RequestComment comment = RequestComment.builder()
                .request(request)
                .author(master)
                .text("Причина отказа: " + reason)
                .createdAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    @Transactional
    public void addComment(Long id, User author, String text) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        RequestComment comment = RequestComment.builder()
                .request(request)
                .author(author)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    public Request getRequestByIdForUser(Long id, User currentUser) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));

        boolean isResident = request.getResident().getId().equals(currentUser.getId());
        boolean isMaster = request.getMaster() != null && request.getMaster().getId().equals(currentUser.getId());

        if (!isResident && !isMaster) {
            throw new RuntimeException("Доступ запрещён");
        }

        return request;
    }
}

