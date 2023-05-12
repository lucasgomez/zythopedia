package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.ProducerDto;
import ch.fdb.zythopedia.dto.creation.CreateProducerDto;
import ch.fdb.zythopedia.entity.Producer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = OriginMapper.class)
public interface ProducerMapper {

    @Mapping(target = "currentEditionAvailableCount", ignore = true)
    @Mapping(target = "currentEditionCount", ignore = true)
    ProducerDto toDto(Producer entity);
    @Mapping(target = "originId", source = "origin.id")
    @Mapping(target = "originName", source = "origin.name")
    @Mapping(target = "originShortName", source = "origin.shortName")
    CreateProducerDto toCreateDto(ProducerDto dto);
}
