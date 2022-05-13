package ch.fdb.zythopedia.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateProducerDto {
    private String name;
    private Long originId;
    private String originShortName;
}
