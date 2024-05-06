package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.dto.creation.CreateStyleDto;
import ch.fdb.zythopedia.dto.mapper.StyleFlatMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.entity.Style;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.service.BoughtDrinkService;
import ch.fdb.zythopedia.service.StyleService;
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
public class StyleController {

    private final StyleService styleService;
    private final StyleFlatMapper styleFlatMapper;
    private final BoughtDrinkService boughtDrinkService;

    public StyleController(StyleService styleService, StyleFlatMapper styleFlatMapper, BoughtDrinkService boughtDrinkService) {
        this.styleService = styleService;
        this.styleFlatMapper = styleFlatMapper;
        this.boughtDrinkService = boughtDrinkService;
    }

    @GetMapping(value = "/style")
    public List<StyleDto> findAll() {
        return styleService.findAll().stream()
                .map(styleFlatMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/edition/current/style")
    public List<StyleDto> findStyleWithService() {
        return boughtDrinkService.findCurrentEditionList(
                boughtDrink -> Optional.ofNullable(boughtDrink)
                        .map(BoughtDrink::getDrink)
                        .map(Drink::getStyle)
                        .orElse(null),
                styleFlatMapper::toDto
        );
    }

    @GetMapping(value = "/style/{styleId}")
    public StyleDto findById(@PathVariable long styleId) {
        return styleService.findById(styleId)
                .map(styleFlatMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(styleId, Style.class.getName()));
    }

    @PostMapping(value = "/style")
    @Secured("ROLE_MANAGER")
    public StyleDto create(@RequestBody CreateStyleDto createStyleDto) {
        return styleFlatMapper.toDto(
                styleService.create(createStyleDto));
    }

    @PutMapping(value = "/style/{styleId}")
    @Secured("ROLE_MANAGER")
    public StyleDto update(@PathVariable long styleId, @RequestBody CreateStyleDto createStyleDto) {
        return styleFlatMapper.toDto(
                styleService.update(styleId, createStyleDto));
    }

    @DeleteMapping(value = "/style/{styleId}")
    @Secured("ROLE_MANAGER")
    public void delete(@PathVariable long styleId) {
        styleService.delete(styleId);
    }

}
