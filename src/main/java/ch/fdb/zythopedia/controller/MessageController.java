package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.MessageDto;
import ch.fdb.zythopedia.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/message", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @GetMapping
    public List<MessageDto> getMessages() {
        return messageService.getMessages();
    }

    @Secured("ROLE_MANAGER")
    @PostMapping
    public MessageDto createMessage() {
        throw new RuntimeException("Not implemented");
    }
}
