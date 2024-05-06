package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.dto.creation.CreateColorDto;
import ch.fdb.zythopedia.dto.mapper.ColorMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Color;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.service.BoughtDrinkService;
import ch.fdb.zythopedia.service.ColorService;
import ch.fdb.zythopedia.service.ListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ColorController {

    private final ColorService colorService;
    private final ColorMapper colorMapper;
    private final BoughtDrinkService boughtDrinkService;

    public ColorController(ColorService styleService, ColorMapper colorMapper, BoughtDrinkService boughtDrinkService) {
        this.colorService = styleService;
        this.colorMapper = colorMapper;
        this.boughtDrinkService = boughtDrinkService;
    }

    @GetMapping("/color")
    public List<ColorDto> findAll() {
        return colorService.findAll().stream()
                .map(colorMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/edition/current/color")
    public List<ColorDto> findColorsWithService() {
        return boughtDrinkService.findCurrentEditionList(
                boughtDrink -> Optional.ofNullable(boughtDrink)
                        .map(BoughtDrink::getDrink)
                        .map(Drink::getColor)
                        .orElse(null),
                colorMapper::toDto
        );
    }

    @GetMapping("/color/{styleId}")
    public ColorDto findById(@PathVariable long styleId) {
        return colorService.findById(styleId)
                .map(colorMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(styleId, Color.class.getName()));
    }

    @PostMapping("/color")
    @Secured("ROLE_MANAGER")
    public ColorDto create(@RequestBody CreateColorDto createColorDto) {
        return colorMapper.toDto(
                colorService.create(createColorDto.getName(), createColorDto.getDescription()));
    }

    @PutMapping("/color/{styleId}")
    @Secured("ROLE_MANAGER")
    public ColorDto update(@PathVariable long styleId, @RequestBody CreateColorDto createColorDto) {
        return colorMapper.toDto(
                colorService.update(styleId, createColorDto.getName(), createColorDto.getDescription()));
    }

    @DeleteMapping("/color/{styleId}")
    @Secured("ROLE_MANAGER")
    public void delete(@PathVariable long styleId) {
        colorService.delete(styleId);
    }

}
