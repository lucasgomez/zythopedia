package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.dto.creation.CreateColorDto;
import ch.fdb.zythopedia.dto.mapper.ColorMapper;
import ch.fdb.zythopedia.entity.Color;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.service.ColorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/color", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ColorController {

    private final ColorService colorService;
    private final ColorMapper colorMapper;

    public ColorController(ColorService styleService, ColorMapper colorMapper) {
        this.colorService = styleService;
        this.colorMapper = colorMapper;
    }

    @GetMapping
    public List<ColorDto> findAll() {
        return colorService.findAll().stream()
                .map(colorMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/current")
    public List<ColorDto> findColorsWithService() {
        return colorService.findColorsWithService();
    }

    @GetMapping(value = "/{styleId}")
    public ColorDto findById(@PathVariable long styleId) {
        return colorService.findById(styleId)
                .map(colorMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(styleId, Color.class.getName()));
    }

    @PostMapping
    @Secured("ROLE_MANAGER")
    public ColorDto create(@RequestBody CreateColorDto createColorDto) {
        return colorMapper.toDto(
                colorService.create(createColorDto.getName(), createColorDto.getDescription()));
    }

    @PutMapping(value = "/{styleId}")
    @Secured("ROLE_MANAGER")
    public ColorDto update(@PathVariable long styleId, @RequestBody CreateColorDto createColorDto) {
        return colorMapper.toDto(
                colorService.update(styleId, createColorDto.getName(), createColorDto.getDescription()));
    }

    @DeleteMapping(value = "/{styleId}")
    @Secured("ROLE_MANAGER")
    public void delete(@PathVariable long styleId) {
        colorService.delete(styleId);
    }

}
