package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.DescriptiveList;
import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.dto.SoldDrinkLightDto;
import ch.fdb.zythopedia.dto.mapper.SoldDrinkLightDtoMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.entity.NamedEntity;
import ch.fdb.zythopedia.entity.Style;
import ch.fdb.zythopedia.enums.Availability;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.repository.ColorRepository;
import ch.fdb.zythopedia.repository.OriginRepository;
import ch.fdb.zythopedia.repository.ProducerRepository;
import ch.fdb.zythopedia.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ListService {

    @Value("${service.drinks.stylesIdsToIgnoreInDisplay}")
    private List<Long> stylesIdsToIgnoreInDisplay;
    private final BoughtDrinkService boughtDrinkService;
    private final SoldDrinkLightDtoMapper soldDrinkLightDtoMapper;
    private final ColorRepository colorRepository;
    private final StyleRepository styleRepository;
    private final ProducerRepository producerRepository;
    private final OriginRepository originRepository;

    public ListService(BoughtDrinkService boughtDrinkService, SoldDrinkLightDtoMapper soldDrinkLightDtoMapper, ColorRepository colorRepository, StyleRepository styleRepository, ProducerRepository producerRepository, OriginRepository originRepository) {
        this.boughtDrinkService = boughtDrinkService;
        this.soldDrinkLightDtoMapper = soldDrinkLightDtoMapper;
        this.colorRepository = colorRepository;
        this.styleRepository = styleRepository;
        this.producerRepository = producerRepository;
        this.originRepository = originRepository;
    }

    public DescriptiveList<SoldDrinkLightDto> findByColorId(Long colorId) {
        return findByListTypeId(colorId, colorRepository, "color", boughtDrinkService::findCurrentEditionByColorId);
    }

    public DescriptiveList<SoldDrinkLightDto> findByStyleId(Long styleId) {
        return findByListTypeId(styleId, styleRepository, "style", boughtDrinkService::findCurrentEditionByStyleId);
    }

    public DescriptiveList<SoldDrinkLightDto> findByProducerId(Long producerId) {
        return findByListTypeId(producerId, producerRepository, "producer", boughtDrinkService::findCurrentEditionByProducerId);
    }

    public DescriptiveList<SoldDrinkLightDto> findByOriginId(Long originId) {
        return findByListTypeId(originId, originRepository, "origin", boughtDrinkService::findCurrentEditionByOriginId);
    }

    public DescriptiveList<SoldDrinkLightDto> findByServiceMethod(ServiceMethod serviceMethod) {
        return DescriptiveList.<SoldDrinkLightDto>builder()
                .title(serviceMethod.getLabel())
                .content(boughtDrinkService.findCurrentEditionByServiceMethod(serviceMethod).stream()
                        .map(soldDrinkLightDtoMapper::toDto)
                        .map(this::sortService)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<SoldDrinkLightDto> getAvailableTapBeers() {
        return getAvailableBeers(ServiceMethod.TAP);
    }

    public List<SoldDrinkLightDto> getAvailableBottledBeers() {
        return getAvailableBeers(ServiceMethod.BOTTLE);
    }

    private List<SoldDrinkLightDto> getAvailableBeers(ServiceMethod serviceMethod) {
        return boughtDrinkService.getCurrentEditionBoughtDrinks(serviceMethod, Availability.AVAILABLE).stream()
                .filter(boughtDrink -> Optional.ofNullable(boughtDrink)
                        .map(BoughtDrink::getDrink)
                        .map(Drink::getStyle)
                        .map(Style::getId)
                        .filter(Predicate.not(stylesIdsToIgnoreInDisplay::contains))
                        .isPresent())
                .map(soldDrinkLightDtoMapper::toDto)
                .sorted(Comparator.comparing(SoldDrinkLightDto::getName))
                .collect(Collectors.toList());
    }

    private DescriptiveList<SoldDrinkLightDto> findByListTypeId(Long listTypeId, CrudRepository<? extends NamedEntity, Long> repository,
                                                                String entityName, Function<Long, List<BoughtDrink>> finder) {
        var listType = repository.findById(listTypeId)
                .orElseThrow(() -> new EntityNotFoundException(listTypeId, entityName));
        var list = finder.apply(listTypeId).stream()
                .map(soldDrinkLightDtoMapper::toDto)
                .map(this::sortService)
                .sorted(Comparator.comparing(SoldDrinkLightDto::getName))
                .collect(Collectors.toList());

        return DescriptiveList.<SoldDrinkLightDto>builder()
                .title(listType.getName())
                .description(listType.getDescription())
                .content(list)
                .build();
    }

    private SoldDrinkLightDto sortService(SoldDrinkLightDto soldDrinkLightDto) {
        Collections.sort(soldDrinkLightDto.getServices(), Comparator.comparing(ServiceDto::getVolumeInCl));
        return soldDrinkLightDto;
    }

    public Set<SoldDrinkDetailedDto> getRandom(Integer count) {
        return boughtDrinkService.getRandom(count);
    }
}
