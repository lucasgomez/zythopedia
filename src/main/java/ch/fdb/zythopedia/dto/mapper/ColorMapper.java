package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.entity.Color;
import org.mapstruct.Mapper;

@Mapper
public interface ColorMapper {

    ColorDto toDto(Color entity);
}
