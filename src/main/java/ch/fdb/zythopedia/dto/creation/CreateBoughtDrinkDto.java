package ch.fdb.zythopedia.dto.creation;

import ch.fdb.zythopedia.enums.ServiceMethod;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class CreateBoughtDrinkDto {

    private String code;
    private String name;
    private String producerName;
    private String producerOriginName;
    private ServiceMethod serviceMethod;
    private String styleName;
    private Double abv;
    private Double buyingPrice;
    private Long volumeInCl;
    private boolean returnable;
}
