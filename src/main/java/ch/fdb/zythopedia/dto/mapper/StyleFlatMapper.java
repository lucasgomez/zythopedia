package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.dto.creation.CreateStyleDto;
import ch.fdb.zythopedia.entity.Style;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StyleFlatMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    @Mapping(target = "currentEditionAvailableCount", ignore = true)
    @Mapping(target = "currentEditionCount", ignore = true)
    StyleDto toDto(Style entity);

    CreateStyleDto toCreateDto(StyleDto dto);
}
