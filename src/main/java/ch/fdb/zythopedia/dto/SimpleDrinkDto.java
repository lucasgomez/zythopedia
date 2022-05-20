package ch.fdb.zythopedia.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class SimpleDrinkDto {
    private Long id;
    private String name;
    private Long producerId;
    private String producerName;
    private String description;
    private Double abv;
    private Long colorId;
    private String colorName;
    private Long styleId;
    private String styleName;
    private Long sourness;
    private Long bitterness;
    private Long sweetness;
    private Long hoppiness;
    private List<Long> volumes;
    private List<Double> prices;
}
