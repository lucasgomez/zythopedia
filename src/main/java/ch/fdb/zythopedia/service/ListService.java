package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.*;
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
        var style = styleRepository.findById(styleId)
                .orElseThrow(() -> new EntityNotFoundException(styleId, "style"));

        var childStyles = style.getChildren();

        var drinkLists = new ArrayList<DescriptiveList<SoldDrinkLightDto>>();
        drinkLists.add(findByListTypeId(styleId, styleRepository, "style", boughtDrinkService::findCurrentEditionByStyleId));

        if (childStyles == null || childStyles.isEmpty()) {
            return drinkLists.get(0);
        }

        for (Style childStyle : childStyles) {
            drinkLists.add(findByListTypeId(childStyle.getId(), styleRepository, "style", boughtDrinkService::findCurrentEditionByStyleId));
        }

        return DescriptiveList.<SoldDrinkLightDto>builder()
                .title(style.getName())
                .content(drinkLists.stream()
                        .map(DescriptiveList::getContent)
                        .flatMap(List::stream)
                        .toList())
                .description(style.getDescription())
                .build();
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
        return getAvailableBeers(boughtDrinkService.getCurrentEditionBoughtDrinks(ServiceMethod.TAP, Availability.AVAILABLE));
    }

    public List<SoldDrinkLightDto> getAvailableBeers() {
        return getAvailableBeers(boughtDrinkService.getCurrentEditionBoughtDrinks());
    }

    public List<SoldDrinkLightDto> getAvailableBottledBeers() {
        return getAvailableBeers(boughtDrinkService.getCurrentEditionBoughtDrinks(ServiceMethod.BOTTLE, Availability.AVAILABLE));
    }

    private List<SoldDrinkLightDto> getAvailableBeers(Collection<BoughtDrink> drinks) {
        return drinks.stream()
                .filter(boughtDrink -> Optional.ofNullable(boughtDrink)
                        .map(BoughtDrink::getDrink)
                        .map(Drink::getStyle)
                        .map(Style::getId)
                        .filter(Predicate.not(stylesIdsToIgnoreInDisplay::contains))
                        .isPresent())
                .filter(boughtDrink -> Availability.AVAILABLE == boughtDrink.getAvailability())
                .map(soldDrinkLightDtoMapper::toDto)
                .sorted(Comparator
                        .comparing(SoldDrinkLightDto::getStyleName)
                        .thenComparing(SoldDrinkLightDto::getName))
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
