package rccl.diploma.crm.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String baseUploadDir;

    public FileStorageService() {
        this.baseUploadDir = System.getenv("UPLOADS_DIR");
        if (baseUploadDir == null) {
            throw new RuntimeException("UPLOADS_DIR environment variable is not set");
        }
    }

    public String saveFile(MultipartFile file, String subDir) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String uploadDir = baseUploadDir + "/" + subDir + "/";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            file.transferTo(uploadPath.resolve(filename));

            return "/uploads/" + subDir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения файла", e);
        }
    }
}
