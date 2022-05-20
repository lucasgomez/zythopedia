package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.DrinkDto;
import ch.fdb.zythopedia.dto.SimpleDrinkDto;
import ch.fdb.zythopedia.entity.Drink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {StrengthMapper.class})
public interface SimpleDrinkMapper {
    @Mapping(source = "producer.id", target = "producerId")
    @Mapping(source = "producer.name", target = "producerName")
    @Mapping(source = "style.id", target = "styleId")
    @Mapping(source = "style.name", target = "styleName")
    @Mapping(source = "color.id", target = "colorId")
    @Mapping(source = "color.name", target = "colorName")
    SimpleDrinkDto toDto(Drink entitiy);

    @Mapping(source = "producer.id", target = "producerId")
    @Mapping(source = "producer.name", target = "producerName")
    @Mapping(source = "style.id", target = "styleId")
    @Mapping(source = "style.name", target = "styleName")
    @Mapping(source = "color.id", target = "colorId")
    @Mapping(source = "color.name", target = "colorName")
    SimpleDrinkDto toSimplerDto(DrinkDto entitiy);
}
