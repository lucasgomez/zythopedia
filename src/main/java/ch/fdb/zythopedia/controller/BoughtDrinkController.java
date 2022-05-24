package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.enums.Availability;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.service.BoughtDrinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Slf4j
@RestController
@RequestMapping(path = "/api/boughtdrink", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class BoughtDrinkController {

    private BoughtDrinkService boughtDrinkService;

    public BoughtDrinkController(BoughtDrinkService boughtDrinkService) {
        this.boughtDrinkService = boughtDrinkService;
    }

    @GetMapping(value = "/{boughtDrinkId}/detail")
    public SoldDrinkDetailedDto getDetailedDrink(@PathVariable Long boughtDrinkId) {
        return boughtDrinkService.findById(boughtDrinkId)
                .orElseThrow(() -> new EntityNotFoundException(boughtDrinkId, "boughtDrink"));
    }

    @PatchMapping("/{boughtDrinkId}/availability")
    public SoldDrinkDetailedDto changeAvailability(@PathVariable Long boughtDrinkId, @PathParam(value = "availability") Availability availability) {
        return boughtDrinkService.updataAvailability(boughtDrinkId, availability)
                .orElseThrow(() -> new EntityNotFoundException(boughtDrinkId, "boughtDrink"));
    }
}
