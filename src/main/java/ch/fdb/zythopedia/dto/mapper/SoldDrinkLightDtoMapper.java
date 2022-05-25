package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.SoldDrinkLightDto;
import ch.fdb.zythopedia.entity.BoughtDrink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ServiceMapper.class})
public interface SoldDrinkLightDtoMapper {
    @Mapping(target="name", source="drink.name")
    @Mapping(target="producerId", source="drink.producer.id")
    @Mapping(target="producerName", source="drink.producer.name")
    @Mapping(target="abv", source="drink.abv")
    @Mapping(target="colorId", source="drink.color.id")
    @Mapping(target="colorName", source="drink.color.name")
    @Mapping(target="styleId", source="drink.style.id")
    @Mapping(target="styleName", source="drink.style.name")
    @Mapping(target="originFlag", source="drink.producer.origin.flag")
    @Mapping(target="originShortName", source="drink.producer.origin.shortName")
    SoldDrinkLightDto toDto(BoughtDrink entity);
}
