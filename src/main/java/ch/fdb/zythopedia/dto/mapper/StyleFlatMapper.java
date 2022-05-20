package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.entity.Style;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StyleFlatMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    StyleDto toDto(Style entity);
}
