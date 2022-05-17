package ch.fdb.zythopedia.dto;

import ch.fdb.zythopedia.enums.Availability;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ServiceDto {
    private Long volumeInCl;
    private Double price;
    private Availability availability;
}
