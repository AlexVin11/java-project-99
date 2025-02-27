package hexlet.code.service;

import hexlet.code.dto.TaskStatusDTO.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO.TaskStatusDTO;
import hexlet.code.dto.TaskStatusDTO.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
