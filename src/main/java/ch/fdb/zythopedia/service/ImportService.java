package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImportService {

    @Value("${edition.current.name}")
    private String currentEditionName;

    private BoughtDrinkService boughtDrinkService;
    private DrinkService drinkService;
    private StyleService styleService;
    private ColorService colorService;
    private AmsteinImporterService amsteinImporterService;

    public ImportService(BoughtDrinkService boughtDrinkService, DrinkService drinkService, StyleService styleService, ColorService colorService, AmsteinImporterService amsteinImporterService) {
        this.boughtDrinkService = boughtDrinkService;
        this.drinkService = drinkService;
        this.styleService = styleService;
        this.colorService = colorService;
        this.amsteinImporterService = amsteinImporterService;
    }

    public void importAmsteinCatalogData(MultipartFile multipartFile) {
        log.info("Starting import from amstein catalog file");

        var colorsFromImport = amsteinImporterService.readColorsFromCatalogFile(multipartFile);
        log.info(String.format("Colors found in amstein catalog file : %s", colorsFromImport.size()));

        var allColorsNames = colorService.findAll().stream()
                .map(Color::getName)
                .collect(Collectors.toSet());
        var colorsToImport = colorsFromImport.stream()
                .filter(Predicate.not(color -> allColorsNames.contains(color.getName())))
                .collect(Collectors.toSet());
        log.info(String.format("Colors from amstein catalog file to import : %s", colorsToImport.size()));

        var importedColors = colorsToImport.stream()
                .map(colorService::create)
                .collect(Collectors.toSet());
        log.info(String.format("Colors from amstein catalog file imported : %s/%s", importedColors.size(), colorsFromImport.size()));

        var stylesFromImport = amsteinImporterService.readStylesFromCatalogFile(multipartFile);
        log.info(String.format("Styles found in amstein catalog file : %s", stylesFromImport.size()));

        var allStylesName = styleService.findAll().stream()
                .map(Style::getName)
                .collect(Collectors.toSet());
        var stylesToImport = stylesFromImport.stream()
                .filter(Predicate.not(color -> allStylesName.contains(color.getName())))
                .collect(Collectors.toSet());
        log.info(String.format("Styles from amstein catalog file to import : %s", stylesToImport.size()));

        var importedStyles = stylesToImport.stream()
                .map(styleService::create)
                .collect(Collectors.toSet());
        log.info(String.format("Styles from amstein catalog file imported : %s/%s", importedStyles.size(), stylesFromImport.size()));

        var drinksToImport = amsteinImporterService.readDrinkFromCatalogFile(multipartFile);
        log.info(String.format("Drinks from amstein catalog file read : %s", drinksToImport.size()));

        var boughtDrinksUpdated = boughtDrinkService.updateBoughtDrinksVolume(drinksToImport.keySet());
        log.info(String.format("BoughtDrinks volume from amstein catalog file imported : %s", boughtDrinksUpdated.size()));

        var updatedDrinks = drinksToImport.entrySet().stream()
                .map(drinkByBoughtDrink -> Pair.of(
                        boughtDrinkService.findCurrentEditionBoughtDrinkByCode(drinkByBoughtDrink.getKey().getCode()),
                        drinkByBoughtDrink.getValue()))
                .filter(drinkByBoughtDrink -> drinkByBoughtDrink.getFirst().map(BoughtDrink::getDrink).isPresent())
                .map(drinkByBoughtDrink -> drinkService.update(drinkByBoughtDrink.getFirst().get().getDrink(), drinkByBoughtDrink.getSecond()))
                .collect(Collectors.toSet());

        log.info(String.format("Drinks updated (%s/%s)", updatedDrinks.size(), drinksToImport.size()));
        log.info("Amstein catalog import finished");
    }

    public void testImportAmsteinOrder(MultipartFile multipartFile) {
        //TODO add method to test reading data
    }

    public void importAmsteinOrder(MultipartFile multipartFile) {
        log.info("Starting import from amstein order file");

        var boughtDrinkToImports = amsteinImporterService.readBoughtDrinkFromOrderFile(multipartFile);

        log.info(String.format("Trying to import %s records", boughtDrinkToImports.size()));

        var allBoughtDrinksByCode = boughtDrinkService.findAll().stream()
                .collect(Collectors.groupingBy(BoughtDrink::getCode));

        var unreferencedBoughtDrinks = getUnreferencedBoughtDrinksToImport(boughtDrinkToImports, allBoughtDrinksByCode);
        log.info(String.format("Unreferenced drinks to import : %s", unreferencedBoughtDrinks.size()));

        var referencedFromPreviousEditionBoughtDrinks = getReferencedBoughtDrinksToImport(boughtDrinkToImports, allBoughtDrinksByCode, false);
        log.info(String.format("Referenced drinks from previous edition import : %s", referencedFromPreviousEditionBoughtDrinks.size()));

        var referencedFromCurrentEditionBoughtDrinks = getReferencedBoughtDrinksToImport(boughtDrinkToImports, allBoughtDrinksByCode, true);
        log.info(String.format("Referenced drinks from current edition to import : %s", referencedFromCurrentEditionBoughtDrinks.size()));

        var createdDrinks = drinkService.createNewDrinks(unreferencedBoughtDrinks);

        boughtDrinkService.createNewBoughtDrinks(matchDrinkWithBoughtDrink(unreferencedBoughtDrinks, createdDrinks));
        log.info("Unreferenced drinks imported");
        boughtDrinkService.createNewBoughtDrinks(mapPairToDrink(referencedFromPreviousEditionBoughtDrinks));
        log.info("Referenced drinks from previous edition imported");
        var updatedBoughtDrinks = boughtDrinkService.updateBoughtDrinksPrice(referencedFromCurrentEditionBoughtDrinks);
        log.info(String.format("Referenced drinks from current edition updated (%s/%s)", updatedBoughtDrinks.size(), referencedFromCurrentEditionBoughtDrinks.size()));

        log.info("End of import");
    }

    private Collection<Pair<CreateBoughtDrinkDto, Drink>> mapPairToDrink(Collection<Pair<CreateBoughtDrinkDto, BoughtDrink>> referencedFromPreviousEditionBoughtDrinks) {
        return referencedFromPreviousEditionBoughtDrinks.stream()
                .map(pair -> Pair.of(pair.getFirst(), pair.getSecond().getDrink()))
                .collect(Collectors.toList());
    }

    private Collection<Pair<CreateBoughtDrinkDto, Drink>> matchDrinkWithBoughtDrink(Collection<CreateBoughtDrinkDto> unreferencedBoughtDrinks, Collection<Drink> createdDrinks) {
        return unreferencedBoughtDrinks.stream()
                .map(boughtDrink -> Pair.of(
                        boughtDrink,
                        findMatchingDrink(createdDrinks, boughtDrink)))
                .collect(Collectors.toList());
    }

    private Drink findMatchingDrink(Collection<Drink> createdDrinks, CreateBoughtDrinkDto boughtDrink) {
        return createdDrinks.stream()
                .filter(createdDrink -> createdDrink.getName().equalsIgnoreCase(boughtDrink.getName()))
                .findFirst().orElseThrow(() -> new RuntimeException(String.format("Could not find matching drink for %s - %s", boughtDrink.getCode(), boughtDrink.getName())));
    }

    private List<Pair<CreateBoughtDrinkDto, BoughtDrink>> getReferencedBoughtDrinksToImport(Collection<CreateBoughtDrinkDto> boughtDrinkToImports, Map<String, List<BoughtDrink>> allBoughtDrinksByCode, boolean isFromCurrentEdition) {
        return boughtDrinkToImports.stream()
                .filter(boughtDrinkToImport -> Objects.nonNull(allBoughtDrinksByCode.get(boughtDrinkToImport.getCode())))
                .map(boughtDrinkToImport -> Pair.of(
                        boughtDrinkToImport,
                        getBoughtDrinkByEdition(allBoughtDrinksByCode, boughtDrinkToImport, isFromCurrentEdition)))
                .filter(pair -> pair.getSecond().isPresent())
                .map(pair -> Pair.of(pair.getFirst(), pair.getSecond().get()))
                .collect(Collectors.toList());
    }

    private List<CreateBoughtDrinkDto> getUnreferencedBoughtDrinksToImport(Collection<CreateBoughtDrinkDto> boughtDrinkToImports, Map<String, List<BoughtDrink>> allBoughtDrinksByCode) {
        return boughtDrinkToImports.stream()
                .filter(boughtDrinkToImport -> Objects.isNull(allBoughtDrinksByCode.get(boughtDrinkToImport.getCode())))
                .collect(Collectors.toList());
    }

    private Optional<BoughtDrink> getBoughtDrinkByEdition(Map<String, List<BoughtDrink>> allBoughtDrinksByCode, CreateBoughtDrinkDto boughtDrinkToImport, boolean wantsCurrentEdition) {
        return allBoughtDrinksByCode.get(boughtDrinkToImport.getCode())
                .stream()
                .filter(existingBoughtDrink -> isFromCurrentEdition(existingBoughtDrink) == wantsCurrentEdition)
                .min(Comparator.comparing(BoughtDrink::getId, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private boolean isFromCurrentEdition(BoughtDrink boughtDrink) {
        return Optional.of(boughtDrink)
                .map(BoughtDrink::getEdition)
                .map(Edition::getName)
                .map(currentEditionName::equalsIgnoreCase)
                .orElse(false);
    }
}
