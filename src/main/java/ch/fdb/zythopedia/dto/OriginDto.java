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
public class OriginDto implements HasId {

    private long id;
    private String name;
    private String shortName;
    private String flag;
}
