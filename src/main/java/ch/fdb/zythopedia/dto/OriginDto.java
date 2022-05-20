package ch.fdb.zythopedia.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
public class OriginDto {

    private long id;
    private String name;
    private String shortName;
    private String flag;
}
