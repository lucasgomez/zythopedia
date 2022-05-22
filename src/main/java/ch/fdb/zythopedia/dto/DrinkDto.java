package ch.fdb.zythopedia.dto;

import ch.fdb.zythopedia.entity.NamedEntity;
import ch.fdb.zythopedia.enums.Strength;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@Builder
public class DrinkDto implements NamedEntity {
    private Long id;
    private String name;
    private ProducerDto producer;
    private String description;
    private Double abv;
    private ColorDto color;
    private StyleDto style;
    private Strength sourness;
    private Strength bitterness;
    private Strength sweetness;
    private Strength hoppiness;
    private List<ServiceDto> services;
}
