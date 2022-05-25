package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.enums.Availability;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.service.BoughtDrinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class BoughtDrinkController {

    private BoughtDrinkService boughtDrinkService;

    public BoughtDrinkController(BoughtDrinkService boughtDrinkService) {
        this.boughtDrinkService = boughtDrinkService;
    }

    @GetMapping("/boughtdrink/{boughtDrinkId}/availability/{availability}")
    public SoldDrinkDetailedDto changeAvailability(@PathVariable Long boughtDrinkId, @PathVariable(value = "availability") Availability availability) {
        return boughtDrinkService.updataAvailability(boughtDrinkId, availability)
                .orElseThrow(() -> new EntityNotFoundException(boughtDrinkId, "boughtDrink"));
    }

    @PostMapping("/boughtdrink")
    public SoldDrinkDetailedDto create(@PathParam(value = "drinkName") String drinkName,
                                       @PathParam(value = "buyingPrice") Double buyingPrice,
                                       @PathParam(value = "serviceMethod") ServiceMethod serviceMethod,
                                       @PathParam(value = "code") String code,
                                       @PathParam(value = "volumeInCl") Long volumeInCl,
                                       @PathParam(value = "producerName") String producerName,
                                       @PathParam(value = "styleName") String styleName,
                                       @PathParam(value = "colorName") String colorName) {
        return boughtDrinkService.create(drinkName, buyingPrice, serviceMethod, code, volumeInCl,
                producerName, styleName, colorName);
    }

    @PostMapping("/drink/{drinkId}/boughtdrink")
    public SoldDrinkDetailedDto createBoughtDrink(@PathVariable Long drinkId,
                                       @PathParam(value = "code") String code,
                                       @PathParam(value = "buyingPrice") Double buyingPrice,
                                       @PathParam(value = "serviceMethod") ServiceMethod serviceMethod,
                                       @PathParam(value = "volumeInCl") Long volumeInCl) {
        return boughtDrinkService.create(drinkId, buyingPrice, serviceMethod, code, volumeInCl);
    }

    @DeleteMapping("/{boughtDrinkId}")
    public void delete(@PathVariable Long boughtDrinkId) {
        boughtDrinkService.delete(boughtDrinkId);
    }
}
