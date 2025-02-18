package hexlet.code.app.service;

import hexlet.code.app.dto.LabelDTO.LabelDTO;
import hexlet.code.app.dto.LabelDTO.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        List<Label> labels = labelRepository.findAll();
        List<LabelDTO> result = labels.stream()
                .map(labelMapper::map)
                .toList();
        return result;
    }

    public LabelDTO findById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id: " + id + " not found."));
        LabelDTO dto = labelMapper.map(label);
        return dto;
    }

    public LabelDTO create(LabelCreateDTO labelData) {
        Label label = labelMapper.map(labelData);
        labelRepository.save(label);
        LabelDTO dto = labelMapper.map(label);
        return dto;
    }

    public LabelDTO update(LabelUpdateDTO labelData, Long id) {
        Label model = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id: " + id + " not found."));
        labelMapper.update(labelData, model);
        labelRepository.save(model);
        LabelDTO dto = labelMapper.map(model);
        return dto;
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
