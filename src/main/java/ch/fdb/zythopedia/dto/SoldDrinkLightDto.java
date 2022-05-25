package ch.fdb.zythopedia.dto;

import ch.fdb.zythopedia.enums.Availability;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SoldDrinkLightDto {
    private Long id;
    private String name;
    private Long producerId;
    private String producerName;
    private String originFlag;
    private String originShortName;
    private Double abv;
    private Long colorId;
    private String colorName;
    private Long styleId;
    private String styleName;
    private Availability availability;
    private List<ServiceDto> services;
}
