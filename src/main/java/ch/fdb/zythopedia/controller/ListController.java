package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.DescriptiveList;
import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.dto.SoldDrinkLightDto;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.service.BoughtDrinkService;
import ch.fdb.zythopedia.service.ListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ListController {

    private final ListService listService;
    private final BoughtDrinkService boughtDrinkService;

    public ListController(ListService listService, BoughtDrinkService boughtDrinkService) {
        this.listService = listService;
        this.boughtDrinkService = boughtDrinkService;
    }

    @GetMapping(value = "/boughtdrink/{boughtDrinkId}/detail")
    public SoldDrinkDetailedDto getDetailedDrink(@PathVariable Long boughtDrinkId) {
        return boughtDrinkService.findById(boughtDrinkId)
                .orElseThrow(() -> new EntityNotFoundException(boughtDrinkId, "boughtDrink"));
    }

    @GetMapping("/service/{serviceMethod}/drink")
    public DescriptiveList<SoldDrinkLightDto> getDrinksByType(@PathVariable ServiceMethod serviceMethod) {
        return listService.findByServiceMethod(serviceMethod);
    }

    @GetMapping("/drink/random")
    public Set<SoldDrinkDetailedDto> getOneRandom() {
        return listService.getRandom(1);
    }

    @GetMapping("/drink/random/{count}")
    public Set<SoldDrinkDetailedDto> getRandom(@PathVariable Integer count) {
        return listService.getRandom(count);
    }

    @GetMapping(value = "/color/{colorId}/drink")
    public DescriptiveList<SoldDrinkLightDto> getDrinksByColor(@PathVariable Long colorId) {
        return listService.findByColorId(colorId);
    }

    @GetMapping(value = "/style/{styleId}/drink")
    public DescriptiveList<SoldDrinkLightDto> getDrinksByStyle(@PathVariable Long styleId) {
        return listService.findByStyleId(styleId);
    }

    @GetMapping(value = "/producer/{producerId}/drink")
    public DescriptiveList<SoldDrinkLightDto> getDrinksByProducer(@PathVariable Long producerId) {
        return listService.findByProducerId(producerId);
    }

    @GetMapping(value = "/origin/{originId}/drink")
    public DescriptiveList<SoldDrinkLightDto> getDrinksByOrigin(@PathVariable Long originId) {
        return listService.findByOriginId(originId);
    }

    @GetMapping(value = "/service/tap/beer/available")
    public List<SoldDrinkLightDto> getAvailableTabBeers() {
        return listService.getAvailableTapBeers();
    }

    @GetMapping(value = "/service/bottle/beer/available")
    public List<SoldDrinkLightDto> getAvailableBottledBeers() {
        return listService.getAvailableBottledBeers();
    }

    @GetMapping(value = "/beer/available")
    public List<SoldDrinkLightDto> getAvailableBeers() {
        return listService.getAvailableBeers();
    }
}
