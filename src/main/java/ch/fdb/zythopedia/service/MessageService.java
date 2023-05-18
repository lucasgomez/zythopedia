package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.MessageDto;
import ch.fdb.zythopedia.dto.mapper.MessageMapper;
import ch.fdb.zythopedia.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    public List<MessageDto> getMessages() {
        return messageRepository.findAll().stream().map(messageMapper::toDto).collect(Collectors.toList());
    }
}
