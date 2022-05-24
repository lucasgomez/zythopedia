package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.dto.mapper.SoldDrinkDetailedDtoMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.entity.Edition;
import ch.fdb.zythopedia.enums.Availability;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.repository.BoughtDrinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BoughtDrinkService {

    private static final double CENTS_PRECISION = 0.01d;

    private BoughtDrinkRepository boughtDrinkRepository;
    private SoldDrinkDetailedDtoMapper soldDrinkDetailedDtoMapper;
    private ServiceService serviceService;
    private EditionService editionService;

    public BoughtDrinkService(BoughtDrinkRepository boughtDrinkRepository, SoldDrinkDetailedDtoMapper soldDrinkDetailedDtoMapper, ServiceService serviceService, EditionService editionService) {
        this.boughtDrinkRepository = boughtDrinkRepository;
        this.soldDrinkDetailedDtoMapper = soldDrinkDetailedDtoMapper;
        this.serviceService = serviceService;
        this.editionService = editionService;
    }

    @Transactional
    public Collection<BoughtDrink> createNewBoughtDrinks(Collection<Pair<CreateBoughtDrinkDto, Drink>> unreferencedBoughtDrinks) {
        var currentEdition = editionService.getCurrentEdition();

        var createdBoughtDrinks = unreferencedBoughtDrinks.stream()
                .filter(boughtDrinkRepository -> Objects.nonNull(boughtDrinkRepository.getFirst().getServiceMethod()))
                .map(boughtDrinkToCreate -> createBoughtDrink(boughtDrinkToCreate, currentEdition))
                .map(boughtDrinkRepository::save)
                .collect(Collectors.toList());

        createServiceIfNeeded(createdBoughtDrinks);

        return createdBoughtDrinks;
    }

    private void createServiceIfNeeded(Collection<BoughtDrink> createdBoughtDrinks) {
        createdBoughtDrinks.stream()
                .filter(this::isReadyForServiceCreation)
                .forEach(boughtDrink -> serviceService.createNeededService(boughtDrink));
    }

    private boolean isReadyForServiceCreation(BoughtDrink boughtDrink) {
        return ServiceMethod.TAP == boughtDrink.getServiceMethod()
                || Optional.ofNullable(boughtDrink.getVolumeInCl()).filter(volume -> 0 < volume).isPresent();
    }

    private BoughtDrink createBoughtDrink(Pair<CreateBoughtDrinkDto, Drink> boughtDrinkToCreate, Edition currentEdition) {
        return BoughtDrink.builder()
                .code(boughtDrinkToCreate.getFirst().getCode())
                .serviceMethod(boughtDrinkToCreate.getFirst().getServiceMethod())
                .returnable(boughtDrinkToCreate.getFirst().getReturnable())
                .buyingPrice(boughtDrinkToCreate.getFirst().getBuyingPrice())
                .drink(boughtDrinkToCreate.getSecond())
                .edition(currentEdition)
                .build();
    }

    public Collection<BoughtDrink> findAll() {
        return boughtDrinkRepository.findAll();
    }

    public List<BoughtDrink> updateBoughtDrinksPrice(Collection<Pair<CreateBoughtDrinkDto, BoughtDrink>> referencedFromCurrentEditionBoughtDrinks) {
        return referencedFromCurrentEditionBoughtDrinks.stream()
                .filter(hasPriceChanged())
                .map(pair -> pair.getSecond().setBuyingPrice(pair.getFirst().getBuyingPrice()))
                .map(boughtDrinkRepository::save)
                .collect(Collectors.toList());
    }

    private Predicate<Pair<CreateBoughtDrinkDto, BoughtDrink>> hasPriceChanged() {
        return pair -> Precision.equals(pair.getFirst().getBuyingPrice(), pair.getSecond().getBuyingPrice(), CENTS_PRECISION);
    }

    public Set<BoughtDrink> updateBoughtDrinksVolume(Collection<CreateBoughtDrinkDto> drinksToUpdate) {
        var allBoughtDrinksByCode = boughtDrinkRepository.findAll().stream()
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

    public Collection<BoughtDrink> getBoughtDrinks(String editionName) {
        var edition = editionService.findEdition(editionName)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Edition %s does not exist. Yet.", editionName)));
        return boughtDrinkRepository.findByEdition(edition);
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

    @Transactional
    public Optional<SoldDrinkDetailedDto> updataAvailability(Long boughDrinkId, Availability availability) {
        return boughtDrinkRepository.findById(boughDrinkId)
                .map(boughtDrink -> boughtDrink.setAvailability(availability))
                .map(soldDrinkDetailedDtoMapper::toDto);
    }

    public Void deleteByDrinkId(Long drinkId, IdOrNameDto unused) {
        var boughtDrinkToDelete = boughtDrinkRepository.findByDrinkId(drinkId)
                .orElseThrow(() -> new EntityNotFoundException(drinkId, "boughtDrink"));
        boughtDrinkToDelete.getServices().forEach(service -> serviceService.delete(service));
        boughtDrinkRepository.delete(boughtDrinkToDelete);
        return null;
    }
}
