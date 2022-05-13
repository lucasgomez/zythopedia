package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.dto.mapper.StyleMapper;
import ch.fdb.zythopedia.service.StyleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/style", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class StyleController {

    private StyleService styleService;
    private StyleMapper styleMapper;

    public StyleController(StyleService styleService, StyleMapper styleMapper) {
        this.styleService = styleService;
        this.styleMapper = styleMapper;
    }

    @GetMapping
    public List<StyleDto> findAll() {
        return styleService.findAll().stream()
                .map(styleMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{styleId}")
    public StyleDto findById(@PathVariable long styleId) {
        return styleService.findById(styleId)
                .map(styleMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }
}
