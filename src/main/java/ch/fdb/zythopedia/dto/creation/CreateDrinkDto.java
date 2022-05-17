package ch.fdb.zythopedia.dto.creation;

import ch.fdb.zythopedia.enums.Strength;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDrinkDto {
    private String name;
    private String producerName;
    private String description;
    private Double abv;
    private String colorName;
    private String styleName;
    private Strength sourness;
    private Strength bitterness;
    private Strength sweetness;
    private Strength hoppiness;
}
