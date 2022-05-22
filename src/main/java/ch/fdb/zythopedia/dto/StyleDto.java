package ch.fdb.zythopedia.dto;

import ch.fdb.zythopedia.entity.HasId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@EqualsAndHashCode
public class StyleDto implements HasId {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
}
