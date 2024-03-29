package ch.fdb.zythopedia.dto;

import ch.fdb.zythopedia.entity.NamedEntity;
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
public class OriginDto implements NamedEntity, EnumerableDto<OriginDto> {

    private Long id;
    private String name;
    private String shortName;
    private String flag;
    private Integer currentEditionAvailableCount;
    private Integer currentEditionCount;
}
