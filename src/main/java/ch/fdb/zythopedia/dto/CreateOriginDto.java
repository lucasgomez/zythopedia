package ch.fdb.zythopedia.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateOriginDto {
    private String name;
    private String shortName;
    private String flag;
}
