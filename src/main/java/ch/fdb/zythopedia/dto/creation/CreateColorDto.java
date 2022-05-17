package ch.fdb.zythopedia.dto.creation;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateColorDto {

    private String name;
    private String description;
}
