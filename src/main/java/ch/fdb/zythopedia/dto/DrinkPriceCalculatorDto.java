package ch.fdb.zythopedia.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DrinkPriceCalculatorDto {
    private Long id;
    private Long boughtDrinkId;
    private String producerName;
    private String producerOriginName;
    private String name;
    private String colorName;
    private String styleName;
    private Double abv;
    private Double buyingPrice;
    private Long volumeInCl;
    private String serviceMethod;
    private Double sellingPrice;
}
