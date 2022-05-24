package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.entity.Service;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceMapper {
    ServiceDto toDto(Service service);
}
