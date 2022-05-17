package ch.fdb.zythopedia.dto.creation;

import ch.fdb.zythopedia.enums.ServiceMethod;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateBoughtDrinkDto {

    private String code;
    private String name;
    private Double buyingPrice;
    private Long volumeInCl;
    private ServiceMethod serviceMethod;

}
