package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.DrinkDto;
import ch.fdb.zythopedia.dto.SoldDrinkDetailedDto;
import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.dto.creation.CreateDrinkDto;
import ch.fdb.zythopedia.dto.creation.FullDrinkDto;
import ch.fdb.zythopedia.dto.mapper.DrinkMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Color;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.entity.NamedEntity;
import ch.fdb.zythopedia.enums.Strength;
import ch.fdb.zythopedia.exceptions.EntityNotFoundException;
import ch.fdb.zythopedia.repository.*;
import liquibase.pro.packaged.F;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DrinkService {

    private final DrinkRepository drinkRepository;
    private final ColorRepository colorRepository;
    private final ProducerRepository producerRepository;
    private final StyleRepository styleRepository;
    private final DrinkMapper drinkMapper;
    private final ColorService colorService;

    public DrinkService(DrinkRepository drinkRepository, ColorRepository colorRepository, ProducerRepository producerRepository, StyleRepository styleRepository, DrinkMapper drinkMapper, ColorService colorService) {
        this.drinkRepository = drinkRepository;
        this.colorRepository = colorRepository;
        this.producerRepository = producerRepository;
        this.styleRepository = styleRepository;
        this.drinkMapper = drinkMapper;
        this.colorService = colorService;
    }

    public List<Drink> findAll() {
        return drinkRepository.findAll();
    }

    public List<Drink> findDrinksWithNoService() {
        return drinkRepository.findAll().stream()
                .filter(drink -> Optional.of(drink)
                        .map(Drink::getBoughtDrinks)
                        .map(List::isEmpty)
                        .orElse(true))
                .collect(Collectors.toList());
    }

    public Collection<Drink> createNewDrinks(Collection<CreateBoughtDrinkDto> unreferencedBoughtDrinks) {
        return unreferencedBoughtDrinks.stream()
                .map(this::mapFromBoughtDrink)
                .map(drinkRepository::save)
                .collect(Collectors.toList());
    }

    public Drink create(DrinkDto dto) {
        return createDrink(drinkMapper.toCreateDto(dto));
    }

    public Drink createDrink(CreateDrinkDto createDrinkDto) {
        return drinkRepository.save(Drink.builder()
                .name(createDrinkDto.getName())
                .description(createDrinkDto.getDescription())
                .abv(createDrinkDto.getAbv())
                .color(Optional.ofNullable(createDrinkDto.getColorName())
                        .flatMap(colorRepository::findByNameIgnoreCase)
                        .orElse(null))
                .producer(Optional.ofNullable(createDrinkDto.getProducerName())
                        .flatMap(producerRepository::findByNameIgnoreCase)
                        .orElse(null))
                .style(Optional.ofNullable(createDrinkDto.getStyleName())
                        .flatMap(styleRepository::findByNameIgnoreCase)
                        .orElse(null))
                .sourness(createDrinkDto.getSourness())
                .bitterness(createDrinkDto.getBitterness())
                .sweetness(createDrinkDto.getSweetness())
                .hoppiness(createDrinkDto.getHoppiness())
                .build());
    }

    public void delete(long id) {
        drinkRepository
                .findById(id)
                .ifPresent(drinkRepository::delete);
    }

    private Drink mapFromBoughtDrink(CreateBoughtDrinkDto boughtDrinkDto) {
        return Drink.builder()
                .name(boughtDrinkDto.getName())
                .build();
    }

    public Drink update(DrinkDto drinkData) {
        var drinkToUpdate = drinkRepository.findById(drinkData.getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No drink of id %s", drinkData.getId())));
        try {
            return drinkRepository.save(drinkToUpdate
                    .setName(Optional.ofNullable(drinkData.getName()).orElse(drinkToUpdate.getName()))
                    .setDescription(Optional.ofNullable(drinkData.getDescription()).orElse(drinkToUpdate.getDescription()))
                    .setAbv(Optional.ofNullable(drinkData.getAbv()).orElse(drinkToUpdate.getAbv()))
                    .setColor(Optional.ofNullable(findEntityFromDto(drinkData.getColor(), colorRepository)).orElse(drinkToUpdate.getColor()))
                    .setProducer(Optional.ofNullable(findEntityFromDto(drinkData.getProducer(), producerRepository)).orElse(drinkToUpdate.getProducer()))
                    .setStyle(Optional.ofNullable(findEntityFromDto(drinkData.getStyle(), styleRepository)).orElse(drinkToUpdate.getStyle()))
                    .setSourness(Optional.ofNullable(drinkData.getSourness()).orElse(drinkToUpdate.getSourness()))
                    .setBitterness(Optional.ofNullable(drinkData.getBitterness()).orElse(drinkToUpdate.getBitterness()))
                    .setSweetness(Optional.ofNullable(drinkData.getSweetness()).orElse(drinkToUpdate.getSweetness()))
                    .setHoppiness(Optional.ofNullable(drinkData.getHoppiness()).orElse(drinkToUpdate.getHoppiness())));
        } catch (PropertyValueException exception) {
            log.error(String.format("Failed to update drink %s (field %s)", drinkData.getId(), exception.getPropertyName()), exception);
            throw new IllegalArgumentException(String.format("Failed to update drink %s", drinkData.getId()), exception);
        }
    }

    private <E extends NamedEntity, D extends NamedEntity> E findEntityFromDto(
            D dto, IdOrNameDtoFinderAndDeleter<E> repository) {
        return Optional.ofNullable(dto)
                .map(NamedEntity::getId)
                .flatMap(repository::findById)
                .orElseGet(() -> Optional.ofNullable(dto)
                        .map(NamedEntity::getName)
                        .flatMap(repository::findByNameIgnoreCase)
                        .orElse(null));
    }

    public Optional<Drink> findById(Long drinkId) {
        return drinkRepository.findById(drinkId);
    }

    public Drink createDrink(CreateBoughtDrinkDto orderWithoutDrink) {
        return createDrink(getDrink(orderWithoutDrink));
    }

    private CreateDrinkDto getDrink(CreateBoughtDrinkDto orderedDrink) {
        return CreateDrinkDto.builder()
                .name(orderedDrink.getName())
                .producerName(orderedDrink.getProducerName())
                .styleName(orderedDrink.getStyleName())
                .abv(orderedDrink.getAbv())
                .build();
    }

    public Optional<Drink> findByNameAndProducerName(String name, String producerName) {
        return drinkRepository.findByNameIgnoreCaseAndProducerNameIgnoreCase(name, producerName);
    }

    public void updateDrink(Drink drinkToUpdate, FullDrinkDto boughtDrinkSubmitted) {
        updatePropertyIfNeeded(drinkToUpdate, boughtDrinkSubmitted.getColorId(),
                Drink::getColor, Drink::setColor, colorRepository::findById);
        updatePropertyIfNeeded(drinkToUpdate, boughtDrinkSubmitted.getStyleId(),
                Drink::getStyle, Drink::setStyle, styleRepository::findById);
        updatePropertyIfNeeded(drinkToUpdate, boughtDrinkSubmitted.getProducerId(),
                Drink::getProducer, Drink::setProducer, producerRepository::findById);

        drinkToUpdate.setName(boughtDrinkSubmitted.getName());
        drinkToUpdate.setDescription(boughtDrinkSubmitted.getDescription());
        drinkToUpdate.setAbv(boughtDrinkSubmitted.getAbv());
        drinkToUpdate.setSourness(Strength.getStrengthByRank(boughtDrinkSubmitted.getSourness()));
        drinkToUpdate.setBitterness(Strength.getStrengthByRank(boughtDrinkSubmitted.getBitterness()));
        drinkToUpdate.setSweetness(Strength.getStrengthByRank(boughtDrinkSubmitted.getSweetness()));
        drinkToUpdate.setHoppiness(Strength.getStrengthByRank(boughtDrinkSubmitted.getHoppiness()));
    }

    private <T extends NamedEntity> void updatePropertyIfNeeded(Drink drink,
                                            Long propertyId,
                                            Function<Drink, T> drinkPropertyGetter,
                                            BiFunction<Drink, T, Drink> drinkPropertySetter,
                                            Function<Long, Optional<T>> propertyFinder) {
        if (propertyId == null) {
            drinkPropertySetter.apply(drink, null);
            return;
        }

        var existingPropertyId = Optional.ofNullable(drinkPropertyGetter.apply(drink))
                .map(NamedEntity::getId)
                .orElse(null);

        if (propertyId.equals(existingPropertyId)) {
            return;
        }

        propertyFinder.apply(propertyId)
                .ifPresent(propertyEntity -> drinkPropertySetter.apply(drink, propertyEntity));
    }
}
