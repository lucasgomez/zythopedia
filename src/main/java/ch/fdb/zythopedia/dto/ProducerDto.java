package ch.fdb.zythopedia.dto;

import ch.fdb.zythopedia.entity.HasId;
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
public class ProducerDto implements HasId {

    private Long id;
    private String name;
    private OriginDto origin;
}
