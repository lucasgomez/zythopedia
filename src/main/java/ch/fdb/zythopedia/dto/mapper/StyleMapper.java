package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.entity.Style;
import org.mapstruct.Mapper;

@Mapper
public interface StyleMapper {

    StyleDto toDto(Style entity);
}
