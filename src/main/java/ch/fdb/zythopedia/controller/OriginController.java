package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.dto.CreateColorDto;
import ch.fdb.zythopedia.dto.CreateOriginDto;
import ch.fdb.zythopedia.dto.OriginDto;
import ch.fdb.zythopedia.dto.mapper.ColorMapper;
import ch.fdb.zythopedia.dto.mapper.OriginMapper;
import ch.fdb.zythopedia.service.ColorService;
import ch.fdb.zythopedia.service.OriginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class OriginController {

    private OriginService originService;
    private OriginMapper originMapper;

    public OriginController(OriginService styleService, OriginMapper originMapper) {
        this.originService = styleService;
        this.originMapper = originMapper;
    }

    @GetMapping(value = "/origin")
    public List<OriginDto> findAll() {
        return originService.findAll().stream()
                .map(originMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/origin/{styleId}")
    public OriginDto findById(@PathVariable long styleId) {
        return originService.findById(styleId)
                .map(originMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    @PostMapping(value = "/origin")
    public OriginDto create(@RequestBody CreateOriginDto createOriginDto) {
        return originMapper.toDto(
                originService.create(createOriginDto));
    }

    @PutMapping(value = "/origin/{styleId}")
    public OriginDto update(@PathVariable long styleId, @RequestBody CreateOriginDto createOriginDto) {
        return originMapper.toDto(
                originService.update(styleId, createOriginDto));
    }

    @DeleteMapping(value = "/origin/{originId}")
    public void delete(@PathVariable long originId) {
        originService.delete(originId);
    }
}
