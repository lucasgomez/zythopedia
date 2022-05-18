package ch.fdb.zythopedia.dto.creation;

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
    private String originName;
}
