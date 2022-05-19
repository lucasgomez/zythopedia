package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.DrinkPriceCalculatorDto;
import ch.fdb.zythopedia.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DrinkPriceCalculatorDtoMapper {
    @Mapping(target = "boughtDrinkId", source = "boughtDrink.id")
    @Mapping(target = "producerName", source = "boughtDrink.drink.producer.name")
    @Mapping(target = "producerOriginName", source = "boughtDrink.drink.producer.origin.name")
    @Mapping(target = "name", source = "boughtDrink.drink.name")
    @Mapping(target = "colorName", source = "boughtDrink.drink.color.name")
    @Mapping(target = "styleName", source = "boughtDrink.drink.style.name")
    @Mapping(target = "abv", source = "boughtDrink.drink.abv")
    @Mapping(target = "buyingPrice", source = "boughtDrink.buyingPrice")
    @Mapping(target = "serviceMethod", source = "boughtDrink.serviceMethod")
    DrinkPriceCalculatorDto toDto(Service entity);
}
