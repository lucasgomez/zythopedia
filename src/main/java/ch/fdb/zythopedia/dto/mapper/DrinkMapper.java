package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.DrinkDto;
import ch.fdb.zythopedia.entity.Drink;
import org.mapstruct.Mapper;

@Mapper(uses = {ProducerMapper.class, ColorMapper.class, StyleFlatMapper.class})
public interface DrinkMapper {
    DrinkDto toDto(Drink entitiy);
}
