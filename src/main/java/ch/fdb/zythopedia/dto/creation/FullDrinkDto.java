package ch.fdb.zythopedia.dto.creation;

import ch.fdb.zythopedia.enums.Availability;
import ch.fdb.zythopedia.enums.ServiceMethod;
import lombok.Data;

@Data
public class FullDrinkDto {
    private Long id;
    private Double buyingPrice;
    private ServiceMethod serviceMethod;
    private Long volumeInCl;
    private Availability availability;
    private String location;

    private Long drinkId;
    private String name;
    private String description;
    private Double abv;
    private Long producerId;
    private Long colorId;
    private Long styleId;
    private Long sourness;
    private Long bitterness;
    private Long sweetness;
    private Long hoppiness;
}
