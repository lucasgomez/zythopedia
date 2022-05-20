package ch.fdb.zythopedia.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
public class StyleDto {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
}
