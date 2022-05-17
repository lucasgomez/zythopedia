package ch.fdb.zythopedia.dto.creation;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateStyleDto {

    private String name;
    private String description;
    private Long parentStyleId;
    private String parentName;
}
