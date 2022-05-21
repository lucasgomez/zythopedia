package ch.fdb.zythopedia.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Builder
@EqualsAndHashCode
public class ColorDto {

    private Long id;
    private String name;
    private String description;
}
