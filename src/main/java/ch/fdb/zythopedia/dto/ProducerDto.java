package ch.fdb.zythopedia.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProducerDto {

    private long id;
    private String name;
    private OriginDto origin;
}
