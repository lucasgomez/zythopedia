package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.repository.DrinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DrinkService {

    private DrinkRepository drinkRepository;

    public DrinkService(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    public Collection<Drink> createNewDrinks(Collection<CreateBoughtDrinkDto> unreferencedBoughtDrinks) {
        return unreferencedBoughtDrinks.stream()
                .map(this::mapFromBoughtDrink)
                .map(drinkRepository::save)
                .collect(Collectors.toList());
    }

    private Drink mapFromBoughtDrink(CreateBoughtDrinkDto boughtDrinkDto) {
        return Drink.builder()
                .name(boughtDrinkDto.getName())
                .build();
    }
}
