package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.ProducerDto;
import ch.fdb.zythopedia.entity.Producer;
import org.mapstruct.Mapper;

@Mapper(uses = OriginMapper.class)
public interface ProducerMapper {

    ProducerDto toDto(Producer entity);
}
