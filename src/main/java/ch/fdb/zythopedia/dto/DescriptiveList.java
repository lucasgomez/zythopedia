package ch.fdb.zythopedia.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DescriptiveList<D> {

    private String title;
    private String description;
    private List<D> content;

}
