package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.dto.creation.CreateStyleDto;
import ch.fdb.zythopedia.dto.mapper.StyleFlatMapper;
import ch.fdb.zythopedia.service.StyleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class StyleController {

    private StyleService styleService;
    private StyleFlatMapper styleFlatMapper;

    public StyleController(StyleService styleService, StyleFlatMapper styleFlatMapper) {
        this.styleService = styleService;
        this.styleFlatMapper = styleFlatMapper;
    }

    @GetMapping(value = "/style")
    public List<StyleDto> findAll() {
        return styleService.findAll().stream()
                .map(styleFlatMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/style/{styleId}")
    public StyleDto findById(@PathVariable long styleId) {
        return styleService.findById(styleId)
                .map(styleFlatMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    @PostMapping(value = "/style")
    public StyleDto create(@RequestBody CreateStyleDto createStyleDto) {
        return styleFlatMapper.toDto(
                styleService.create(createStyleDto));
    }

    @PutMapping(value = "/style/{styleId}")
    public StyleDto update(@PathVariable long styleId, @RequestBody CreateStyleDto createStyleDto) {
        return styleFlatMapper.toDto(
                styleService.update(styleId, createStyleDto));
    }

    @DeleteMapping(value = "/style/{styleId}")
    public void delete(@PathVariable long styleId) {
        styleService.delete(styleId);
    }

}
