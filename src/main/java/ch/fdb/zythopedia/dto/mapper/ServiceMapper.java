package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ServiceMapper {

    @Mapping(target = "drinkName", source = "boughtDrink.drink.name")
    @Mapping(target = "producerName", source = "boughtDrink.drink.producer.name")
    @Mapping(target = "availability", source = "boughtDrink.availability")
    @Mapping(target = "boughtDrinkId", source = "boughtDrink.id")
    @Mapping(target = "location", source = "boughtDrink.location")
    ServiceDto toDto(Service service);
}
