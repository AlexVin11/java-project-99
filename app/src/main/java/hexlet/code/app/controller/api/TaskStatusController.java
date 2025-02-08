package hexlet.code.app.controller.api;

import hexlet.code.app.dto.TaskStatusDTO.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusDTO.TaskStatusUpdateDTO;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task_statuses")
public final class TaskStatusController {
    private final TaskStatusService taskStatusService;

    @GetMapping()
    public ResponseEntity<List<TaskStatusDTO>> index() {
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatusService.getAll().size()))
                .body(taskStatusService.getAll());
    }

    @GetMapping("/{id}")
    public TaskStatusDTO show(@PathVariable Long id) {
        return taskStatusService.getById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO taskStatusData) {
        var dto = taskStatusService.create(taskStatusData);
        return dto;
    }

    @PutMapping("/{id}")
    public TaskStatusDTO update(@Valid @RequestBody TaskStatusUpdateDTO taskStatusData, @PathVariable Long id) {
        var dto = taskStatusService.update(taskStatusData, id);
        return dto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}
