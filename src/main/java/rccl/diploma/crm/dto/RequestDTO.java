package rccl.diploma.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import rccl.diploma.crm.entity.enums.RequestType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Тип заявки не может быть пустым")
    private RequestType type;

    private MultipartFile[] photos;
}
