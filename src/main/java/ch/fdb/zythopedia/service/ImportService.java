package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.entity.*;
import ch.fdb.zythopedia.utils.SpreadsheetHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ch.fdb.zythopedia.utils.SpreadsheetHelper.getWorkbookFromFile;

@Slf4j
@Service
public class ImportService {

    @Value("${edition.current.name}")
    private String currentEditionName;

    private final BoughtDrinkService boughtDrinkService;
    private final DrinkService drinkService;
    private final StyleService styleService;
    private final ColorService colorService;
    private final OriginService originService;
    private final ProducerService producerService;
    private final AmsteinReaderService amsteinReaderService;
    private final DrinkDataReaderService drinkDataReaderService;
    private final PricesCalculatorReaderService pricesCalculatorReaderService;
    private final ServiceService serviceService;
    private final OrderReaderService orderReaderService;

    public ImportService(BoughtDrinkService boughtDrinkService, DrinkService drinkService, StyleService styleService, ColorService colorService, OriginService originService, ProducerService producerService, AmsteinReaderService amsteinReaderService, DrinkDataReaderService drinkDataReaderService, PricesCalculatorReaderService pricesCalculatorReaderService, ServiceService serviceService, OrderReaderService orderReaderService) {
        this.boughtDrinkService = boughtDrinkService;
        this.drinkService = drinkService;
        this.styleService = styleService;
        this.colorService = colorService;
        this.originService = originService;
        this.producerService = producerService;
        this.amsteinReaderService = amsteinReaderService;
        this.drinkDataReaderService = drinkDataReaderService;
        this.pricesCalculatorReaderService = pricesCalculatorReaderService;
        this.serviceService = serviceService;
        this.orderReaderService = orderReaderService;
    }

    public void importOrder(MultipartFile file) {
        log.info("Starting import of order");

        var workbook = getWorkbookFromFile(file);

        var allDrinksByCode = boughtDrinkService.findAll().stream()
                .filter(boughtDrink -> Objects.nonNull(boughtDrink.getDrink()))
                .filter(boughtDrink -> Objects.nonNull(boughtDrink.getCode()))
                .filter(Predicate.not(boughtDrink -> boughtDrink.getCode().isEmpty()))
                .collect(Collectors.toMap(
                        BoughtDrink::getCode,
                        BoughtDrink::getDrink,
                        (bd1, bd2) -> bd1.getId() < bd2.getId() ? bd1 : bd2));

        var orders = orderReaderService.readDrinksOrders(workbook);
        var ordersWithoutDrink = orders.stream()
                .filter(hasExistingDrink(allDrinksByCode))
                .collect(Collectors.toSet());

        updateOrdersWithProducers(ordersWithoutDrink);
        updateOrdersWithStyles(ordersWithoutDrink);

        var ordersWithDrink = findOrCreateDrinksWithOrder(ordersWithoutDrink, orders, allDrinksByCode);

        var currentEditionBoughtDrinks = boughtDrinkService.getCurrentEditionBoughtDrinks();
        var currentBoughtDrinksCode = currentEditionBoughtDrinks.stream()
                .map(BoughtDrink::getCode)
                .filter(Strings::isNotBlank)
                .collect(Collectors.toSet());
        var currentBoughtDrinksDrinkId = currentEditionBoughtDrinks.stream()
                .filter(boughtDrink -> Objects.nonNull(boughtDrink.getDrink()))
                .collect(Collectors.toMap(boughtDrink -> boughtDrink.getDrink().getId(), boughtDrink -> boughtDrink));

        var boughtDrinksToCreate = ordersWithDrink.entrySet().stream()
                .filter(Predicate.not(order -> boughtDrinkExists(order.getKey(), order.getValue(), currentBoughtDrinksCode, currentBoughtDrinksDrinkId.keySet())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var boughtDrinksToUpdate = ordersWithDrink.entrySet().stream()
                .filter(order -> boughtDrinkExists(order.getKey(), order.getValue(), currentBoughtDrinksCode, currentBoughtDrinksDrinkId.keySet()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> currentBoughtDrinksDrinkId.get(entry.getValue().getId())));
        boughtDrinkService.createNewBoughtDrinks(boughtDrinksToCreate);

        boughtDrinkService.updateBoughtDrinksPrice(boughtDrinksToUpdate);

        log.info("End import of order");
    }

    private boolean boughtDrinkExists(CreateBoughtDrinkDto createBoughtDrinkDto, Drink drink, Set<String> currentBoughtDrinksCode, Set<Long> currentBoughtDrinksDrinkId) {
        return
                Optional.ofNullable(createBoughtDrinkDto)
                        .map(CreateBoughtDrinkDto::getCode)
                        .filter(currentBoughtDrinksCode::contains)
                        .isPresent() ||
                Optional.ofNullable(drink)
                        .map(Drink::getId)
                        .filter(currentBoughtDrinksDrinkId::contains)
                        .isPresent();
    }

    private Map<CreateBoughtDrinkDto, Drink> findOrCreateDrinksWithOrder(Set<CreateBoughtDrinkDto> ordersWithoutDrink, List<CreateBoughtDrinkDto> orders, Map<String, Drink> allDrinksByCode) {
        var ordersWithDrink = ordersWithoutDrink.stream()
                .collect(Collectors.toMap(
                        order -> order,
                        this::findOrCreateDrink));
        orders.stream()
                .filter(Predicate.not(ordersWithDrink::containsKey))
                .forEach(existingOrder -> ordersWithDrink.put(
                        existingOrder,
                        allDrinksByCode.get(existingOrder.getCode())));

        return ordersWithDrink;
    }

    private Drink findOrCreateDrink(CreateBoughtDrinkDto order) {
        return drinkService
                .findByNameAndProducerName(order.getName(), order.getProducerName())
                .orElseGet(() -> drinkService.createDrink(order));
    }

    private static Predicate<CreateBoughtDrinkDto> hasExistingDrink(Map<String, Drink> drinksByCode) {
        return order ->
                Objects.isNull(order.getCode()) || Objects.isNull(drinksByCode.get(order.getCode()));
    }

    private void updateOrdersWithStyles(Set<CreateBoughtDrinkDto> ordersWithoutDrink) {
        ordersWithoutDrink.stream()
                .filter(order -> Objects.nonNull(order.getStyleName()))
                .filter(Predicate.not(order -> order.getStyleName().isBlank()))
                .forEach(order -> order.setStyleName(
                        styleService.findOrCreate(order.getStyleName())
                                .getName()));
    }

    private void updateOrdersWithProducers(Set<CreateBoughtDrinkDto> ordersWithoutDrink) {
        ordersWithoutDrink.stream()
                .filter(order -> Objects.nonNull(order.getProducerName()))
                .filter(Predicate.not(order -> order.getProducerName().isBlank()))
                .forEach(order -> order.setProducerName(
                        producerService.findOrCreate(order.getProducerName(), order.getProducerOriginName())
                                .getName()));
    }

    public void importPrices(MultipartFile file) {
        log.info("Starting import of prices");

        var workbook = getWorkbookFromFile(file);
        var pricesToUpdate = pricesCalculatorReaderService.readServices(workbook);

        log.info(String.format("Prices to import read %s", pricesToUpdate.size()));
        var servicesUpdated = pricesToUpdate.stream()
                .map(serviceService::updatePrice)
                .toList();
        log.info(String.format("Prices to imported %s / %s", servicesUpdated.size(), pricesToUpdate.size()));

        log.info("End of import of prices");
    }

    public void importDrinksData(MultipartFile file) {
        log.info("Starting import of drinks data");

        var workbook = getWorkbookFromFile(file);
        processColorsToImport(workbook);
        processOriginsToImport(workbook);
        processProducersToImport(workbook);
        processStylesToImport(workbook);
        processDrinksToImport(workbook);

        log.info("End of import of drinks data");
    }

    private <D extends NamedEntity, E> void processDataToImport(String entityName, Collection<Row> originRows,
                                                                Function<Collection<Row>, Collection<D>> reader,
                                                                Function<Collection<Row>, Map<Long, IdOrNameDto>> readerForDeletionWithReplacement,
                                                                Function<D, E> updater, Function<D, E> creater,
                                                                BiFunction<Long, IdOrNameDto, Void> deleter) {
        var readDtos = reader.apply(originRows);

        log.info(String.format("%s to update %s", entityName, readDtos.size()));
        var entitiesToUpdate = readDtos.stream()
                .filter(hasId())
                .collect(Collectors.toSet());
        var updatedEntities = entitiesToUpdate.stream()
                .map(updater)
                .collect(Collectors.toSet());
        log.info(String.format("%s updated %s/%s", entityName, updatedEntities.size(), entitiesToUpdate.size()));

        var entitiesToCreate = readDtos.stream()
                .filter(Predicate.not(hasId()))
                .collect(Collectors.toSet());
        log.info(String.format("%s to create %s", entityName, entitiesToCreate.size()));

        Set<E> createdEntities = new HashSet<>();
        for (var entityToCreate : entitiesToCreate) {
            try {
                var createdEntity = creater.apply(entityToCreate);
                createdEntities.add(createdEntity);
            } catch (Exception exception) {
                log.error(String.format("Error while creating %s %s, probably a duplicate (error : %s)",
                        entityName, entityToCreate.getName(), exception.getMessage()));
            }
        }

        log.info(String.format("Created %s %s/%s", entityName, createdEntities.size(), entitiesToCreate.size()));

        var entitiesToDeleteWitheEntityToTransferTo = readerForDeletionWithReplacement.apply(originRows);
        entitiesToDeleteWitheEntityToTransferTo.forEach(deleter::apply);
    }

    private void processOriginsToImport(Workbook workbook) {
        processDataToImport("Origins", drinkDataReaderService.getOriginRows(workbook),
                drinkDataReaderService::readOrigins,
                drinkDataReaderService::readOriginsToDeleteWithReplacement,
                originService::update, originService::create, originService::delete);
    }

    private void processProducersToImport(Workbook workbook) {
        processDataToImport("Producers", drinkDataReaderService.getProducerRows(workbook),
                drinkDataReaderService::readProducers,
                drinkDataReaderService::readProducersToDeleteWithReplacement,
                producerService::update, producerService::create, producerService::delete);
    }

    private void processStylesToImport(Workbook workbook) {
        processDataToImport("Styles", drinkDataReaderService.getStyleRows(workbook),
                drinkDataReaderService::readStyles,
                drinkDataReaderService::readStylesToDeleteWithReplacement,
                styleService::update, styleService::create, styleService::delete);
    }

    private void processColorsToImport(Workbook workbook) {
        processDataToImport("Colors", drinkDataReaderService.getColorRows(workbook),
                drinkDataReaderService::readColors,
                drinkDataReaderService::readColorsToDeleteWithReplacement,
                colorService::update, colorService::create, colorService::delete);
    }

    private void processDrinksToImport(Workbook workbook) {
        processDataToImport("Drinks", drinkDataReaderService.getDrinkRows(workbook),
                drinkDataReaderService::readDrinks,
                drinkDataReaderService::readDrinksToDelete,
                drinkService::update, drinkService::create,
                boughtDrinkService::deleteByDrinkId);

    }

    private Predicate<NamedEntity> hasId() {
        return origin -> Objects.nonNull(origin.getId());
    }

    public void importAmsteinCatalogData(MultipartFile multipartFile) {
        log.info("Starting import from amstein catalog file");

        var sheet = SpreadsheetHelper.getWorkbookFromFile(multipartFile).getSheetAt(0);
        importColors(sheet);
        importStyles(sheet);
        importProducers(sheet);
        importDrinks(sheet);

        log.info("Amstein catalog import finished");
    }

    public void importAmsteinOrder(MultipartFile multipartFile) {
        log.info("Starting import from amstein order file");

        var sheet = SpreadsheetHelper.getWorkbookFromFile(multipartFile).getSheetAt(0);
        var boughtDrinkToImports = amsteinReaderService.readBoughtDrinkFromOrderFile(sheet);

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
        boughtDrinkService.createNewBoughtDrinks(mapBoughtDrinksToDrinks(referencedFromPreviousEditionBoughtDrinks));
        log.info("Referenced drinks from previous edition imported");
        var updatedBoughtDrinks = boughtDrinkService.updateBoughtDrinksPrice(referencedFromCurrentEditionBoughtDrinks);
        log.info(String.format("Referenced drinks from current edition updated (%s/%s)", updatedBoughtDrinks.size(), referencedFromCurrentEditionBoughtDrinks.size()));

        log.info("End of import");
    }


    private void importDrinks(Sheet sheet) {
        var drinksToImport = amsteinReaderService.readDrinkFromCatalogFile(sheet);
        log.info(String.format("Drinks from amstein catalog file read : %s", drinksToImport.size()));

        var boughtDrinksUpdated = boughtDrinkService.updateBoughtDrinksVolume(drinksToImport.keySet());
        log.info(String.format("BoughtDrinks volume from amstein catalog file imported : %s", boughtDrinksUpdated.size()));

        var updatedDrinks = drinksToImport.entrySet().stream()
                .map(drinkByBoughtDrink -> drinkByBoughtDrink.getValue()
                        .setId(boughtDrinkService.findCurrentEditionBoughtDrinkByCode(drinkByBoughtDrink.getKey().getCode())
                                .map(BoughtDrink::getDrink)
                                .map(Drink::getId)
                                .orElse(null)))
                .filter(drinkDto -> Objects.nonNull(drinkDto.getId()))
                .map(drinkService::update)
                .collect(Collectors.toSet());

        log.info(String.format("Drinks updated (%s/%s)", updatedDrinks.size(), drinksToImport.size()));
    }

    private void importProducers(Sheet sheet) {
        var producerFromImport = amsteinReaderService.readProducersFromCatalogFile(sheet);
        log.info(String.format("Producers found in amstein catalog file : %s", producerFromImport.size()));

        var allProducersName = producerService.findAll().stream()
                .map(Producer::getName)
                .collect(Collectors.toSet());
        var producersToImport = producerFromImport.stream()
                .filter(Predicate.not(producer -> allProducersName.contains(producer.getName())))
                .collect(Collectors.toSet());
        log.info(String.format("Producers from amstein catalog file to import : %s", producersToImport.size()));

        var importedProducers = producersToImport.stream()
                .map(producerService::create)
                .collect(Collectors.toSet());
        log.info(String.format("Producers from amstein catalog file imported : %s/%s", importedProducers.size(), producerFromImport.size()));
    }

    private Collection<Style> importStyles(Sheet sheet) {
        var stylesFromImport = amsteinReaderService.readStylesFromCatalogFile(sheet);
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

        return importedStyles;
    }

    private Collection<Color> importColors(Sheet sheet) {
        var colorsFromImport = amsteinReaderService.readColorsFromCatalogFile(sheet);
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

        return importedColors;
    }

    private Map<CreateBoughtDrinkDto, Drink> mapBoughtDrinksToDrinks(Map<CreateBoughtDrinkDto, BoughtDrink> referencedFromPreviousEditionBoughtDrinks) {
        return referencedFromPreviousEditionBoughtDrinks.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getDrink()));
    }

    private Map<CreateBoughtDrinkDto, Drink> matchDrinkWithBoughtDrink(Collection<CreateBoughtDrinkDto> unreferencedBoughtDrinks, Collection<Drink> createdDrinks) {
        return unreferencedBoughtDrinks.stream()
                .collect(Collectors.toMap(
                        boughtDrink -> boughtDrink,
                        boughtDrink -> findMatchingDrink(createdDrinks, boughtDrink)));
    }

    private Drink findMatchingDrink(Collection<Drink> createdDrinks, CreateBoughtDrinkDto boughtDrink) {
        return createdDrinks.stream()
                .filter(createdDrink -> createdDrink.getName().equalsIgnoreCase(boughtDrink.getName()))
                .findFirst().orElseThrow(() -> new RuntimeException(String.format("Could not find matching drink for %s - %s", boughtDrink.getCode(), boughtDrink.getName())));
    }

    private Map<CreateBoughtDrinkDto, BoughtDrink> getReferencedBoughtDrinksToImport(Collection<CreateBoughtDrinkDto> boughtDrinkToImports, Map<String, List<BoughtDrink>> allBoughtDrinksByCode, boolean isFromCurrentEdition) {
        return boughtDrinkToImports.stream()
                .filter(boughtDrinkToImport -> Objects.nonNull(allBoughtDrinksByCode.get(boughtDrinkToImport.getCode())))
                .map(boughtDrinkToImport -> Pair.of(
                        boughtDrinkToImport,
                        getBoughtDrinkByEdition(allBoughtDrinksByCode, boughtDrinkToImport, isFromCurrentEdition)))
                .filter(pair -> pair.getSecond().isPresent())
                .collect(Collectors.toMap(Pair::getFirst, sa -> sa.getSecond().get()));
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
