package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ServiceMapper {

    @Mapping(target = "drinkName", source = "boughtDrink.drink.name")
    @Mapping(target = "producerName", source = "boughtDrink.drink.producer.name")
    ServiceDto toDto(Service service);
}
