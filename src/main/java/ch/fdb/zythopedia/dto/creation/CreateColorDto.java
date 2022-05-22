package ch.fdb.zythopedia.dto.creation;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@EqualsAndHashCode
public class CreateColorDto {
    private String name;
    private String description;
}
