package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.entity.BoughtDrink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ServiceMapper.class, ProducerMapper.class, ColorMapper.class, StyleFlatMapper.class})
public interface SoldDrinkDetailedDtoMapper {
    @Mapping(target="name", source="drink.name")
    @Mapping(target="abv", source="drink.abv")
    @Mapping(target="description", source="drink.description")
    @Mapping(target="sourness", source="drink.sourness.rank")
    @Mapping(target="bitterness", source="drink.bitterness.rank")
    @Mapping(target="sweetness", source="drink.sweetness.rank")
    @Mapping(target="hoppiness", source="drink.hoppiness.rank")
    @Mapping(target="producer", source="drink.producer")
    @Mapping(target="style", source="drink.style")
    @Mapping(target="color", source="drink.color")
    SoldDrinkDetailedDto toDto(BoughtDrink entity);
}
