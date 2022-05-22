package ch.fdb.zythopedia.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdOrNameDto {
    private Long id;
    private String name;
}
