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
public class ProducerDto implements NamedEntity, EnumerableDto<ProducerDto> {

    private Long id;
    private String name;
    private OriginDto origin;
    private Integer currentEditionAvailableCount;
    private Integer currentEditionCount;
}
