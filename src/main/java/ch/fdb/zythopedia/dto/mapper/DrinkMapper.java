package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.DrinkDto;
import ch.fdb.zythopedia.dto.creation.CreateDrinkDto;
import ch.fdb.zythopedia.entity.Drink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ProducerMapper.class, ColorMapper.class, StyleFlatMapper.class})
public interface DrinkMapper {
    DrinkDto toDto(Drink entitiy);
    @Mapping(target = "colorName", source = "color.name")
    @Mapping(target = "styleName", source = "style.name")
    @Mapping(target = "producerName", source = "producer.name")
    CreateDrinkDto toCreateDto(DrinkDto dto);
}
