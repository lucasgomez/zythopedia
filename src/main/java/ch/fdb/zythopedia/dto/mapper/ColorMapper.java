package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.entity.Color;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ColorMapper {

    @Mapping(target = "currentEditionAvailableCount", ignore = true)
    @Mapping(target = "currentEditionCount", ignore = true)
    ColorDto toDto(Color entity);

}
