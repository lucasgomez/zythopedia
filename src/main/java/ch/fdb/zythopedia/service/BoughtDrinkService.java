package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.dto.EnumerableDto;
import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.dto.creation.CreateDrinkDto;
import ch.fdb.zythopedia.dto.creation.FullDrinkDto;
import ch.fdb.zythopedia.dto.mapper.FullDrinkDtoMapper;
import ch.fdb.zythopedia.dto.mapper.SoldDrinkDetailedDtoMapper;
import ch.fdb.zythopedia.entity.*;
import ch.fdb.zythopedia.enums.Availability;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.repository.BoughtDrinkRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BoughtDrinkService {

    public static final Predicate<BoughtDrink> BOUGHT_DRINK_AVAILABLE = boughtDrink -> boughtDrink.getAvailability() == Availability.AVAILABLE;
    private static final double CENTS_PRECISION = 0.01d;

    private final BoughtDrinkRepository boughtDrinkRepository;
    private final SoldDrinkDetailedDtoMapper soldDrinkDetailedDtoMapper;
    private final FullDrinkDtoMapper fullDrinkDtoMapper;
    private final ServiceService serviceService;
    private final EditionService editionService;
    private final DrinkService drinkService;

    @Value("${service.drinks.stylesIdsToIgnoreInDisplay}")
    private List<Long> stylesIdsToIgnoreInDisplay;

    public BoughtDrinkService(BoughtDrinkRepository boughtDrinkRepository, SoldDrinkDetailedDtoMapper soldDrinkDetailedDtoMapper, FullDrinkDtoMapper fullDrinkDtoMapper, ServiceService serviceService, EditionService editionService, DrinkService drinkService) {
        this.boughtDrinkRepository = boughtDrinkRepository;
        this.soldDrinkDetailedDtoMapper = soldDrinkDetailedDtoMapper;
        this.fullDrinkDtoMapper = fullDrinkDtoMapper;
        this.serviceService = serviceService;
        this.editionService = editionService;
        this.drinkService = drinkService;
    }

    @Transactional
    public Collection<BoughtDrink> createNewBoughtDrinks(Map<CreateBoughtDrinkDto, Drink> unreferencedBoughtDrinks) {
        var currentEdition = editionService.getCurrentEdition();

        var createdBoughtDrinks = unreferencedBoughtDrinks.entrySet().stream()
                .filter(boughtDrinkEntry -> Objects.nonNull(boughtDrinkEntry.getKey().getServiceMethod()))
                .map(boughtDrinkToCreate -> createBoughtDrink(boughtDrinkToCreate.getKey(), boughtDrinkToCreate.getValue(), currentEdition))
                .map(boughtDrinkRepository::save)
                .collect(Collectors.toList());

        createServiceIfNeeded(createdBoughtDrinks);

        return createdBoughtDrinks;
    }

    private void createServiceIfNeeded(Collection<BoughtDrink> createdBoughtDrinks) {
        createdBoughtDrinks.stream()
                .filter(this::isReadyForServiceCreation)
                .forEach(serviceService::createNeededService);
    }

    private boolean isReadyForServiceCreation(BoughtDrink boughtDrink) {
        return ServiceMethod.TAP == boughtDrink.getServiceMethod()
                || Optional.ofNullable(boughtDrink.getVolumeInCl()).filter(volume -> 0 < volume).isPresent();
    }

    private BoughtDrink createBoughtDrink(CreateBoughtDrinkDto boughtDrinkToCreate, Drink drink, Edition currentEdition) {
        var created = boughtDrinkRepository.save(BoughtDrink.builder()
                .code(boughtDrinkToCreate.getCode())
                .serviceMethod(boughtDrinkToCreate.getServiceMethod())
                .returnable(boughtDrinkToCreate.isReturnable())
                .buyingPrice(Optional.ofNullable(boughtDrinkToCreate.getBuyingPrice()).orElse(0.0))
                .drink(drink)
                .volumeInCl(ServiceMethod.TAP == boughtDrinkToCreate.getServiceMethod()
                        ? 100
                        : boughtDrinkToCreate.getVolumeInCl())
                .availability(Availability.AVAILABLE)
                .returnable(false)
                .edition(currentEdition)
                .build());

        createServiceIfNeeded(Collections.singleton(created));
        return created;
    }

    public Collection<BoughtDrink> findAll() {
        return boughtDrinkRepository.findAll();
    }

    public List<BoughtDrink> updateBoughtDrinksPrice(Map<CreateBoughtDrinkDto, BoughtDrink> referencedFromCurrentEditionBoughtDrinks) {
        return referencedFromCurrentEditionBoughtDrinks.entrySet().stream()
                .filter(hasPriceChanged())
                .map(pair -> pair.getValue().setBuyingPrice(pair.getKey().getBuyingPrice()))
                .map(boughtDrinkRepository::save)
                .collect(Collectors.toList());
    }

    private Predicate<Map.Entry<CreateBoughtDrinkDto, BoughtDrink>> hasPriceChanged() {
        return pair -> Precision.equals(pair.getKey().getBuyingPrice(), pair.getValue().getBuyingPrice(), CENTS_PRECISION);
    }

    public Set<BoughtDrink> updateBoughtDrinksVolume(Collection<CreateBoughtDrinkDto> drinksToUpdate) {
        var allBoughtDrinksByCode = boughtDrinkRepository.findAll().stream()
                .filter(boughtDrink -> Optional.ofNullable(boughtDrink)
                        .map(BoughtDrink::getEdition)
                        .map(Edition::getName)
                        .filter(editionName -> editionName.equals(editionService.getCurrentEditionName()))
                        .isPresent())
                .collect(Collectors.toMap(BoughtDrink::getCode, boughtDrink -> boughtDrink));
        var updatedBoughtDrinks = drinksToUpdate.stream()
                .map(drinkToUpdate -> Optional.ofNullable(allBoughtDrinksByCode.get(drinkToUpdate.getCode()))
                        .map(boughtDrink -> boughtDrink.setVolumeInCl(drinkToUpdate.getVolumeInCl()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .map(boughtDrinkRepository::save)
                .collect(Collectors.toSet());

        createServiceIfNeeded(updatedBoughtDrinks);

        return updatedBoughtDrinks;
    }

    public Optional<BoughtDrink> findCurrentEditionBoughtDrinkByCode(String code) {
        return boughtDrinkRepository.findByCodeAndEditionName(code, editionService.getCurrentEditionName());
    }

    public Collection<BoughtDrink> getCurrentEditionBoughtDrinks() {
        return boughtDrinkRepository.findByEdition(editionService.getCurrentEdition());
    }

    public Collection<BoughtDrink> getPreviousEditionsBoughtDrinks() {
        var currentEdition = editionService.getCurrentEdition();
        return boughtDrinkRepository.findAll().stream()
                .filter(boughtDrink -> Optional.of(boughtDrink)
                        .map(BoughtDrink::getEdition)
                        .filter(Predicate.not(currentEdition::equals))
                        .isPresent())
                .collect(Collectors.toSet());
    }

    public List<BoughtDrink> getCurrentEditionBoughtDrinks(ServiceMethod serviceMethod, Availability availability) {
        return boughtDrinkRepository.findByServiceMethodAndEditionNameAndAvailability(
                serviceMethod, editionService.getCurrentEdition().getName(), availability);
    }

    public Collection<BoughtDrink> getBoughtDrinks(String editionName) {
        var edition = editionService.findEdition(editionName)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Edition %s does not exist. Yet.", editionName)));
        return boughtDrinkRepository.findByEdition(edition);
    }

    public List<BoughtDrink> findCurrentEdition() {
        return boughtDrinkRepository.findByEdition(editionService.getCurrentEdition());
    }

    public <D extends EnumerableDto<D> & NamedEntity, E extends NamedEntity> List<D> findCurrentEditionList(Function<BoughtDrink, E> readEntityMapper, Function<E, D> dtoMapper) {
        var currentEditionBoughtDrinks = boughtDrinkRepository.findByEdition(editionService.getCurrentEdition());

        var entities = currentEditionBoughtDrinks.stream()
                .map(readEntityMapper)
                .filter(Objects::nonNull)
                .distinct()
                .map(dtoMapper)
                .toList();
        var availableBoughtDrinksByEntityCount = currentEditionBoughtDrinks.stream()
                .filter(BoughtDrink::isAvailable)
                .filter(boughtDrink -> Objects.nonNull(readEntityMapper.apply(boughtDrink)))
                .collect(Collectors.toMap(
                        boughtDrink -> readEntityMapper.apply(boughtDrink).getId(),
                        boughtDrink -> 1,
                        Integer::sum));
        var boughtDrinkByEntityCount = currentEditionBoughtDrinks.stream()
                .filter(boughtDrink -> Objects.nonNull(readEntityMapper.apply(boughtDrink)))
                .collect(Collectors.toMap(
                        boughtDrink -> readEntityMapper.apply(boughtDrink).getId(),
                        boughtDrink -> 1,
                        Integer::sum));
        return entities.stream()
                .map(dto -> addCounts(dto, availableBoughtDrinksByEntityCount, boughtDrinkByEntityCount))
                .collect(Collectors.toList());

    }

    private <D extends EnumerableDto<D> & NamedEntity> D addCounts(D dto, Map<Long, Integer> availableBoughtDrinksByEntityCount, Map<Long, Integer> boughtDrinkByEntityCount) {
        dto.setCurrentEditionAvailableCount(availableBoughtDrinksByEntityCount.getOrDefault(dto.getId(), 0));
        dto.setCurrentEditionCount(boughtDrinkByEntityCount.getOrDefault(dto.getId(), 0));
        return dto;
    }

    public List<BoughtDrink> findCurrentEditionByColorId(Long colorId) {
        return boughtDrinkRepository.findByDrinkColorIdAndEditionName(colorId, editionService.getCurrentEditionName());
    }

    public List<BoughtDrink> findCurrentEditionByStyleId(Long styleId) {
        return boughtDrinkRepository.findByDrinkStyleIdAndEditionName(styleId, editionService.getCurrentEditionName());
    }

    public List<BoughtDrink> findCurrentEditionByProducerId(Long producerId) {
        return boughtDrinkRepository.findByDrinkProducerIdAndEditionName(producerId, editionService.getCurrentEditionName());
    }

    public List<BoughtDrink> findCurrentEditionByOriginId(Long originId) {
        return boughtDrinkRepository.findByDrinkProducerOriginIdAndEditionName(originId, editionService.getCurrentEditionName());
    }

    public List<BoughtDrink> findCurrentEditionByServiceMethod(ServiceMethod serviceMethod) {
        return boughtDrinkRepository.findByServiceMethodAndEditionName(serviceMethod, editionService.getCurrentEditionName());
    }

    public Optional<ch.fdb.zythopedia.dto.SoldDrinkDetailedDto> findById(Long boughtDrinkId) {
        return boughtDrinkRepository.findById(boughtDrinkId)
                .map(soldDrinkDetailedDtoMapper::toDto);
    }

    public FullDrinkDto getFullBoughtDrink(Long boughtDrinkId) {
        return boughtDrinkRepository.findById(boughtDrinkId)
                .map(fullDrinkDtoMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(boughtDrinkId, "boughtDrink"));

    }

    @Transactional
    public Optional<SoldDrinkDetailedDto> updataAvailability(Long boughDrinkId, Availability availability) {
        return boughtDrinkRepository.findById(boughDrinkId)
                .map(boughtDrink -> boughtDrink.setAvailability(availability))
                .map(soldDrinkDetailedDtoMapper::toDto);
    }

    @Transactional
    public Void deleteByDrinkId(Long drinkId, IdOrNameDto unused) {
        boughtDrinkRepository.findByDrinkId(drinkId)
                .ifPresent(this::delete);
        return null;
    }

    @Transactional
    public void delete(Long boughtDrinkId) {
        var boughtDrinkToDelete = boughtDrinkRepository.findById(boughtDrinkId)
                .orElseThrow(() -> new EntityNotFoundException(boughtDrinkId, "boughtDrink"));
        delete(boughtDrinkToDelete);
    }

    private void delete(BoughtDrink boughtDrinkToDelete) {
        boughtDrinkToDelete.getServices()
                .forEach(serviceService::delete);
        boughtDrinkRepository.delete(boughtDrinkToDelete);
    }

    @Transactional
    public SoldDrinkDetailedDto create(String drinkName, Double buyingPrice, ServiceMethod serviceMethod,
                                       String code, Long volumeInCl, String producerName, String styleName,
                                       String colorName) {
        var createdDrink = drinkService.createDrink(CreateDrinkDto.builder()
                .name(drinkName)
                .producerName(producerName)
                .styleName(styleName)
                .colorName(colorName)
                .build());

        var boughtDrinkToCreate = CreateBoughtDrinkDto.builder()
                .code(code)
                .serviceMethod(serviceMethod)
                .volumeInCl(volumeInCl)
                .buyingPrice(buyingPrice)
                .build();

        return soldDrinkDetailedDtoMapper.toDto(
                createBoughtDrink(boughtDrinkToCreate, createdDrink, editionService.getCurrentEdition()));
    }

    public SoldDrinkDetailedDto create(Long drinkId, Double buyingPrice, ServiceMethod serviceMethod,
                                       String code, Long volumeInCl) {
        var drink = drinkService.findById(drinkId)
                .orElseThrow(() -> new EntityNotFoundException(drinkId, "drink"));

        var boughtDrinkToCreate = CreateBoughtDrinkDto.builder()
                .code(code)
                .serviceMethod(serviceMethod)
                .volumeInCl(volumeInCl)
                .buyingPrice(buyingPrice)
                .build();

        return soldDrinkDetailedDtoMapper.toDto(
                createBoughtDrink(boughtDrinkToCreate, drink, editionService.getCurrentEdition()));
    }

    public Set<SoldDrinkDetailedDto> getRandom(Integer count) {
        return boughtDrinkRepository.findByEdition(editionService.getCurrentEdition())
                .stream()
                .filter(BOUGHT_DRINK_AVAILABLE)
                .filter(boughtDrink -> Optional.ofNullable(boughtDrink.getDrink())
                        .map(Drink::getStyle)
                        .map(Style::getId)
                        .map(styleId -> !stylesIdsToIgnoreInDisplay.contains(styleId))
                        .orElse(false))
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .limit(Optional.ofNullable(count).orElse(1))
                .map(soldDrinkDetailedDtoMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public SoldDrinkDetailedDto updateBoughtDrink(Long boughtDrinkId, FullDrinkDto boughtDrinkDto) {
        var boughtDrink = boughtDrinkRepository.findById(boughtDrinkId)
                .orElseThrow(() -> new EntityNotFoundException(boughtDrinkId, "boughtDrink"));

        drinkService.updateDrink(boughtDrink.getDrink(), boughtDrinkDto);

        boughtDrink
                .setAvailability(boughtDrinkDto.getAvailability())
                .setServiceMethod(boughtDrinkDto.getServiceMethod())
                .setVolumeInCl(boughtDrinkDto.getVolumeInCl())
                .setBuyingPrice(boughtDrinkDto.getBuyingPrice())
                .setLocation(boughtDrinkDto.getLocation());

        return soldDrinkDetailedDtoMapper.toDto(boughtDrinkRepository.save(boughtDrink));
    }

    public void updateBoughtDrinksStatus(Availability availability) {
        log.error("[Panic mode activated!]");
        boughtDrinkRepository.findByEdition(editionService.getCurrentEdition())
                .stream()
                .filter(BOUGHT_DRINK_AVAILABLE)
                .map(boughtDrink -> boughtDrink.setAvailability(availability))
                .forEach(boughtDrinkRepository::save);
    }
}
