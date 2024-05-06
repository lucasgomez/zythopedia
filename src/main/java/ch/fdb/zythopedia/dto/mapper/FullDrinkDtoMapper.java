package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.creation.FullDrinkDto;
import ch.fdb.zythopedia.entity.BoughtDrink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = StrengthMapper.class)
public interface FullDrinkDtoMapper {

    @Mapping(source = "drink.id", target = "drinkId")
    @Mapping(source = "drink.name", target = "name")
    @Mapping(source = "drink.description", target = "description")
    @Mapping(source = "drink.abv", target = "abv")
    @Mapping(source = "drink.producer.id", target = "producerId")
    @Mapping(source = "drink.color.id", target = "colorId")
    @Mapping(source = "drink.style.id", target = "styleId")
    @Mapping(source = "drink.sourness", target = "sourness")
    @Mapping(source = "drink.bitterness", target = "bitterness")
    @Mapping(source = "drink.sweetness", target = "sweetness")
    @Mapping(source = "drink.hoppiness", target = "hoppiness")
    FullDrinkDto toDto(BoughtDrink entity);
}
