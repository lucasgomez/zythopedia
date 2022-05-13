package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.dto.CreateColorDto;
import ch.fdb.zythopedia.dto.mapper.ColorMapper;
import ch.fdb.zythopedia.service.ColorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ColorController {

    private ColorService colorService;
    private ColorMapper colorMapper;

    public ColorController(ColorService styleService, ColorMapper colorMapper) {
        this.colorService = styleService;
        this.colorMapper = colorMapper;
    }

    @GetMapping(value = "/color")
    public List<ColorDto> findAll() {
        return colorService.findAll().stream()
                .map(colorMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/color/{styleId}")
    public ColorDto findById(@PathVariable long styleId) {
        return colorService.findById(styleId)
                .map(colorMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    @PostMapping(value = "/color")
    public ColorDto create(@RequestBody CreateColorDto createColorDto) {
        return colorMapper.toDto(
                colorService.create(createColorDto.getName(), createColorDto.getDescription()));
    }

    @PutMapping(value = "/color/{styleId}")
    public ColorDto update(@PathVariable long styleId, @RequestBody CreateColorDto createColorDto) {
        return colorMapper.toDto(
                colorService.update(styleId, createColorDto.getName(), createColorDto.getDescription()));
    }

    @DeleteMapping(value = "/color/{styleId}")
    public void delete(@PathVariable long styleId) {
        colorService.delete(styleId);
    }

}
