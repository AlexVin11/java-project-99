package hexlet.code.app.dto.TaskDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class TaskCreateDTO {
    @NotNull
    @NotBlank
    @Size(min = 1)
    private String title;

    private Integer index;

    private String content;

    @NotNull
    @NotBlank
    private String status;

    private Long assigneeId;
}
