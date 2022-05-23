package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.DrinkDto;
import ch.fdb.zythopedia.dto.mapper.DrinkMapper;
import ch.fdb.zythopedia.service.DrinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class DrinkController {

    private DrinkService drinkService;
    private DrinkMapper drinkMapper;

    public DrinkController(DrinkService drinkService, DrinkMapper drinkMapper) {
        this.drinkService = drinkService;
        this.drinkMapper = drinkMapper;
    }

    @GetMapping()
    public List<DrinkDto> getAll() {
        return drinkService.findAll().stream()
                .map(drinkMapper::toDto)
                .collect(Collectors.toList());
    }
}
