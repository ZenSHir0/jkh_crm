package rccl.diploma.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsDTO {

    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Срок актуальности должен быть указан")
    private LocalDateTime validUntil;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MultipartFile[] photos;
}
