package hexlet.code.app.mapper;

import hexlet.code.app.dto.LabelDTO.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO.LabelDTO;
import hexlet.code.app.dto.LabelDTO.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper (
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LabelMapper {
    public abstract Label map(LabelCreateDTO createDTO);
    public abstract LabelDTO map(Label model);
    public abstract Label map(LabelDTO dto);
    public abstract void update(LabelUpdateDTO updateDTO, @MappingTarget Label model);
}
