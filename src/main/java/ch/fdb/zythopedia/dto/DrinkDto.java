package ch.fdb.zythopedia.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class DrinkDto {
    private Long id;
    private String name;
    private ProducerDto producer;
    private Double abv;
    private ColorDto color;
    private StyleDto style;
    private List<ServiceDto> services;
}
