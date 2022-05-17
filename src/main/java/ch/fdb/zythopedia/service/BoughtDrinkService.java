package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.entity.Drink;
import ch.fdb.zythopedia.entity.Edition;
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

    public static final double CENTS_PRECISION = 0.01d;
    private BoughtDrinkRepository boughtDrinkRepository;
    private EditionService editionService;

    public BoughtDrinkService(BoughtDrinkRepository boughtDrinkRepository, EditionService editionService) {
        this.boughtDrinkRepository = boughtDrinkRepository;
        this.editionService = editionService;
    }

    @Transactional
    public Collection<BoughtDrink> createNewBoughtDrinks(Collection<Pair<CreateBoughtDrinkDto, Drink>> unreferencedBoughtDrinks) {
        var currentEdition = editionService.getCurrentEdition();

        return unreferencedBoughtDrinks.stream()
                .map(boughtDrinkToCreate -> createBoughtDrink(boughtDrinkToCreate, currentEdition))
                .map(boughtDrinkRepository::save)
                .collect(Collectors.toList());
    }

    private BoughtDrink createBoughtDrink(Pair<CreateBoughtDrinkDto, Drink> boughtDrinkToCreate, Edition currentEdition) {
        return BoughtDrink.builder()
                .code(boughtDrinkToCreate.getFirst().getCode())
                .serviceMethod(boughtDrinkToCreate.getFirst().getServiceMethod())
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
}
