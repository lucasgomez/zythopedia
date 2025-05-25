package ch.fdb.zythopedia.dto;

import ch.fdb.zythopedia.enums.Availability;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SoldDrinkDetailedDto {
    private Long id;
    private String name;
    private String description;
    private Double abv;
    private ProducerDto producer;
    private ColorDto color;
    private StyleDto style;
    private Long sourness;
    private Long bitterness;
    private Long sweetness;
    private Long hoppiness;
    private Availability availability;
    private List<ServiceDto> services;
    private String location;
}
