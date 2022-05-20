package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.dto.creation.CreateDrinkDto;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.repository.ColorRepository;
import ch.fdb.zythopedia.repository.DrinkRepository;
import ch.fdb.zythopedia.repository.ProducerRepository;
import ch.fdb.zythopedia.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DrinkService {

    private DrinkRepository drinkRepository;
    private ColorRepository colorRepository;
    private ProducerRepository producerRepository;
    private StyleRepository styleRepository;

    public DrinkService(DrinkRepository drinkRepository, ColorRepository colorRepository, ProducerRepository producerRepository, StyleRepository styleRepository) {
        this.drinkRepository = drinkRepository;
        this.colorRepository = colorRepository;
        this.producerRepository = producerRepository;
        this.styleRepository = styleRepository;
    }

    public Collection<Drink> createNewDrinks(Collection<CreateBoughtDrinkDto> unreferencedBoughtDrinks) {
        return unreferencedBoughtDrinks.stream()
                .map(this::mapFromBoughtDrink)
                .map(drinkRepository::save)
                .collect(Collectors.toList());
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

    public Drink update(Drink drinkToUpdate, CreateDrinkDto drinkData) {
        try {
            return drinkRepository.save(drinkToUpdate
                    .setName(drinkData.getName())
                    .setDescription(drinkData.getDescription())
                    .setAbv(drinkData.getAbv())
                    .setColor(Optional.ofNullable(drinkData.getColorName())
                            .flatMap(colorRepository::findByName)
                            .orElse(null))
                    .setProducer(Optional.ofNullable(drinkData.getProducerName())
                            .flatMap(producerRepository::findByName)
                            .orElse(null))
                    .setStyle(Optional.ofNullable(drinkData.getStyleName())
                            .flatMap(styleRepository::findByName)
                            .orElse(null))
                    .setSourness(drinkData.getSourness())
                    .setBitterness(drinkData.getBitterness())
                    .setSweetness(drinkData.getSweetness())
                    .setHoppiness(drinkData.getHoppiness()));
        } catch (PropertyValueException exception) {
            log.error(String.format("Failed to update drink %s (field %s)", drinkToUpdate.getId(), exception.getPropertyName()), exception);
            throw new IllegalArgumentException(String.format("Failed to update drink %s", drinkToUpdate.getId()), exception);
        }
    }
}
