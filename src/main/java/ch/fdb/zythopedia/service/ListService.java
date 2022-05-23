package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.DescriptiveList;
import ch.fdb.zythopedia.dto.SoldDrinkLightDto;
import ch.fdb.zythopedia.dto.mapper.SoldDrinkLightDtoMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.NamedEntity;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.repository.ColorRepository;
import ch.fdb.zythopedia.repository.OriginRepository;
import ch.fdb.zythopedia.repository.ProducerRepository;
import ch.fdb.zythopedia.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ListService {

    private BoughtDrinkService boughtDrinkService;
    private SoldDrinkLightDtoMapper soldDrinkLightDtoMapper;
    private ColorRepository colorRepository;
    private StyleRepository styleRepository;
    private ProducerRepository producerRepository;
    private OriginRepository originRepository;

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
                        .collect(Collectors.toList()))
                .build();
    }

    private DescriptiveList<SoldDrinkLightDto> findByListTypeId(Long listTypeId, CrudRepository<? extends NamedEntity, Long> repository,
                                                                String entityName, Function<Long, List<BoughtDrink>> finder) {
        var listType = repository.findById(listTypeId)
                .orElseThrow(() -> new EntityNotFoundException(listTypeId, entityName));
        var list = finder.apply(listTypeId).stream()
                .map(soldDrinkLightDtoMapper::toDto)
                .sorted(Comparator.comparing(SoldDrinkLightDto::getName))
                .collect(Collectors.toList());

        return DescriptiveList.<SoldDrinkLightDto>builder()
                .title(listType.getName())
                .description(listType.getDescription())
                .content(list)
                .build();
    }
}
