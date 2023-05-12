package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ServiceMapper {

    @Mapping(target = "currentEditionAvailableCount", ignore = true)
    @Mapping(target = "currentEditionCount", ignore = true)
    ServiceDto toDto(Service service);
}
