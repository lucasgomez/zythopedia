package ch.fdb.zythopedia.dto;

import ch.fdb.zythopedia.enums.Availability;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@Accessors(chain = true)
public class ServiceDto {
    private Long id;
    private String drinkName;
    private String producerName;
    private Long volumeInCl;
    private Double sellingPrice;
    private Availability availability;
    private Integer currentEditionAvailableCount;
    private Integer currentEditionCount;
}
