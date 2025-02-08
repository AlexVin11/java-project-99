package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusDTO.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusDTO.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        List<TaskStatus> tasks = taskStatusRepository.findAll();
        List<TaskStatusDTO> result = tasks.stream()
                .map(taskStatusMapper::map)
                .toList();
        return result;
    }

    public TaskStatusDTO getById(Long id) {
        TaskStatus model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id: " + id + " not found"));
        TaskStatusDTO dto = taskStatusMapper.map(model);
        return dto;
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        TaskStatus model = taskStatusMapper.map(taskStatusData);
        taskStatusRepository.save(model);
        TaskStatusDTO dto = taskStatusMapper.map(model);
        return dto;
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id) {
        TaskStatus model = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id: " + id + " not found"));
        taskStatusMapper.update(taskStatusData, model);
        taskStatusRepository.save(model);
        TaskStatusDTO dto = taskStatusMapper.map(model);
        return dto;
    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
