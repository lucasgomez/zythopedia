package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.OriginDto;
import ch.fdb.zythopedia.dto.creation.CreateOriginDto;
import ch.fdb.zythopedia.entity.Origin;
import org.mapstruct.Mapper;

@Mapper
public interface OriginMapper {

    OriginDto toDto(Origin entity);
    CreateOriginDto toCreateDto(OriginDto dto);
}
