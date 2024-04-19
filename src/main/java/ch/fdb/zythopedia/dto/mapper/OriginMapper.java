package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.OriginDto;
import ch.fdb.zythopedia.dto.creation.CreateOriginDto;
import ch.fdb.zythopedia.entity.Origin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OriginMapper {

    @Mapping(target = "currentEditionAvailableCount", ignore = true)
    @Mapping(target = "currentEditionCount", ignore = true)
    OriginDto toDto(Origin entity);

    CreateOriginDto toCreateDto(OriginDto dto);
}
