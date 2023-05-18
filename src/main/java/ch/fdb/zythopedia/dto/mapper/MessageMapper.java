package ch.fdb.zythopedia.dto.mapper;

import ch.fdb.zythopedia.dto.MessageDto;
import ch.fdb.zythopedia.entity.Message;
import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper {
    MessageDto toDto(Message message);
}
