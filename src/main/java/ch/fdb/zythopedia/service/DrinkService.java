package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.DrinkDto;
import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.dto.creation.CreateDrinkDto;
import ch.fdb.zythopedia.dto.mapper.DrinkMapper;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.entity.NamedEntity;
import ch.fdb.zythopedia.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DrinkService {

    private DrinkRepository drinkRepository;
    private ColorRepository colorRepository;
    private ProducerRepository producerRepository;
    private StyleRepository styleRepository;
    private DrinkMapper drinkMapper;

    public DrinkService(DrinkRepository drinkRepository, ColorRepository colorRepository, ProducerRepository producerRepository, StyleRepository styleRepository, DrinkMapper drinkMapper) {
        this.drinkRepository = drinkRepository;
        this.colorRepository = colorRepository;
        this.producerRepository = producerRepository;
        this.styleRepository = styleRepository;
        this.drinkMapper = drinkMapper;
    }

    public List<Drink> findAll() {
        return drinkRepository.findAll();
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
                        .flatMap(colorRepository::findByName)
                        .orElse(null))
                .producer(Optional.ofNullable(createDrinkDto.getProducerName())
                        .flatMap(producerRepository::findByName)
                        .orElse(null))
                .style(Optional.ofNullable(createDrinkDto.getStyleName())
                        .flatMap(styleRepository::findByName)
                        .orElse(null))
                .sourness(createDrinkDto.getSourness())
                .bitterness(createDrinkDto.getBitterness())
                .sweetness(createDrinkDto.getSweetness())
                .hoppiness(createDrinkDto.getHoppiness())
                .build());
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
                        .flatMap(repository::findByName)
                        .orElse(null));
    }

    public Optional<Drink> findById(Long drinkId) {
        return drinkRepository.findById(drinkId);
    }
}
