package ch.fdb.zythopedia.dto.creation;

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
}
