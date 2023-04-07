package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.dto.IdOrNameDto;
import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.entity.Color;
import ch.fdb.zythopedia.entity.Style;
import ch.fdb.zythopedia.repository.*;
import ch.fdb.zythopedia.service.DrinkDataReaderService;
import ch.fdb.zythopedia.service.ImporterTestHelper;
import ch.fdb.zythopedia.service.PricesCalculatorReaderService;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ImportControllerTest {

    public static final String API_IMPORT_DATA = "/api/import/data";
    public static final String API_IMPORT_ORDER_AMSTEIN = "/api/import/order/amstein";
    public static final String API_IMPORT_CATALOG_AMSTEIN = "/api/import/catalog/amstein";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DrinkRepository drinkRepository;
    @Autowired
    private BoughtDrinkRepository boughtDrinkRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private StyleRepository styleRepository;
    @Autowired
    private ProducerRepository producerRepository;
    @Autowired
    private OriginRepository originRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @MockBean
    private PricesCalculatorReaderService mockPricesCalculatorReaderService;
    @MockBean
    private DrinkDataReaderService drinkDataReaderService;

    @Test
    void importAmsteinOrder() throws Exception {
        var file = ImporterTestHelper.openFileFromResources(ImporterTestHelper.AMSTEIN_ORDER_XLSX);

        postFile(API_IMPORT_ORDER_AMSTEIN, file);

        var drinks = drinkRepository.findAll();
        assertEquals(68, drinks.size());

        var boughtDrinks = boughtDrinkRepository.findAll();
        assertEquals(68, boughtDrinks.size());

        var riegele = drinks.stream().filter(drink -> drink.getName().contains("Riegele IPA Simco")).findFirst();
        assertTrue(riegele.isPresent());

        postFile(API_IMPORT_ORDER_AMSTEIN, file);

        drinks = drinkRepository.findAll();
        assertEquals(68, drinks.size());

        boughtDrinks = boughtDrinkRepository.findAll();
        assertEquals(68, boughtDrinks.size());
    }

    @Test
    public void testImportAmsteinCatalog() throws Exception {
        var orderFile = ImporterTestHelper.openFileFromResources(ImporterTestHelper.AMSTEIN_ORDER_XLSX);
        postFile(API_IMPORT_ORDER_AMSTEIN, orderFile);
        var catalogFile = ImporterTestHelper.openFileFromResources(ImporterTestHelper.AMSTEIN_CATALOG_XLSX);
        postFile(API_IMPORT_CATALOG_AMSTEIN, catalogFile);

        assertEquals(6, colorRepository.count());
        assertEquals(23, styleRepository.count());
        assertEquals(37, producerRepository.count());
        assertEquals(10, originRepository.count());
        assertEquals(68, drinkRepository.count());
        assertEquals(68, boughtDrinkRepository.count());
    }

    @Test
    public void testImportPricesFromCalculator() throws Exception {
        var pricesCalculatorReaderService = new PricesCalculatorReaderService();

        var orderFile = ImporterTestHelper.openFileFromResources(ImporterTestHelper.AMSTEIN_ORDER_XLSX);
        postFile(API_IMPORT_ORDER_AMSTEIN, orderFile);

        var drinkDataFile = ImporterTestHelper.openFileFromResources(ImporterTestHelper.DRINK_DATA_XLSX);
        postFile(API_IMPORT_DATA, drinkDataFile);

        assertFalse(serviceRepository.findAll().stream()
                .anyMatch(service -> Objects.nonNull(service.getSellingPrice())));

        when(mockPricesCalculatorReaderService.readServices(any()))
                .thenAnswer(invokation -> pricesCalculatorReaderService.readServices(ImporterTestHelper.openXLSXFromResources(ImporterTestHelper.PRICE_CALCULATOR_XLSX)).stream()
                        .map(this::updateServiceId)
                        .collect(Collectors.toList()));

        var calculatorFile = ImporterTestHelper.openFileFromResources(ImporterTestHelper.PRICE_CALCULATOR_XLSX);
        postFile("/api/import/calculator", calculatorFile);

    }

    private ServiceDto updateServiceId(ServiceDto service) {
        var services = serviceRepository.findByBoughtDrinkDrinkNameAndBoughtDrinkDrinkProducerNameAndVolumeInCl(
                service.getDrinkName(), service.getProducerName(), service.getVolumeInCl());
        assertEquals(1, services.size());
        return service.setId(services.get(0).getId());
    }

    @Test
    public void testImportDrinkData() throws Exception {

        postFile(API_IMPORT_ORDER_AMSTEIN, ImporterTestHelper.openFileFromResources(ImporterTestHelper.AMSTEIN_ORDER_XLSX));
        postFile(API_IMPORT_CATALOG_AMSTEIN, ImporterTestHelper.openFileFromResources(ImporterTestHelper.AMSTEIN_CATALOG_XLSX));

        var realDrinkDataReaderService = new DrinkDataReaderService();
        mockColorReading(realDrinkDataReaderService);
        mockStyleReading(realDrinkDataReaderService);

        // Check initial status for colors
        var aromatiseeId = colorRepository.findByName("Aromatisée").map(Color::getId).orElseThrow();
        var noireId = colorRepository.findByName("Noir").map(Color::getId).orElseThrow();
        assertTrue(Strings.isEmpty(colorRepository.findByName("Brune").orElseThrow().getDescription()), "Brune color should not have description yet");

        // Check initial status for styles
        var nestleId = styleRepository.findByName("NESTLE WATERS (SUISSE) SA").map(Style::getId).orElseThrow();
        var ipaId = styleRepository.findByName("India Pale Ale").map(Style::getId).orElseThrow();
        var lager = styleRepository.findByName("Lager / Premium").orElseThrow();
        assertTrue(Strings.isEmpty(lager.getDescription()), "Lager style should not have description yet");

        var file = ImporterTestHelper.openFileFromResources(ImporterTestHelper.DRINK_DATA_XLSX);
        postFile(API_IMPORT_DATA, file);

        // Assert changes for color
        assertTrue(colorRepository.findById(aromatiseeId).isEmpty(), "Color should be deleted");
        assertTrue(colorRepository.findByName("Blanche").isPresent(), "Color should be added");
        assertEquals("Noire", colorRepository.findById(noireId).map(Color::getName).orElse("Not found"), "Color should be added");
        assertFalse(Strings.isEmpty(colorRepository.findByName("Brune").orElseThrow().getDescription()), "Brune color should have description now");

        // Assert changes for style
        assertTrue(styleRepository.findById(nestleId).isEmpty(), "Style should be deleted");
        assertTrue(styleRepository.findByName("Triple Wet Hopped Kölsch").isPresent(), "Style should be added");
        assertEquals("IPA", styleRepository.findById(ipaId).map(Style::getName).orElse("Not found"), "Style name should be updated");
        assertFalse(Strings.isEmpty(styleRepository.findById(lager.getId()).map(Style::getDescription).orElse("Not found")), "Style description should be updated");
    }

    private void mockStyleReading(DrinkDataReaderService realDrinkDataReaderService) {
        var allStylesIdByOldName = styleRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Style::getName,
                        Style::getId));

        when(drinkDataReaderService.readStyles(anyCollection())).thenAnswer(invocationOnMock -> {
            var stylesRead = realDrinkDataReaderService.readStyles((Collection<Row>) invocationOnMock.getArgument(0, Collection.class));
            return stylesRead.stream()
                    .map(style -> style.setId(translateIdFromImport(style.getId(), allStylesIdByOldName, ImporterTestHelper.STYLES_OLD_NAME_BY_ID_FROM_IMPORT)))
                    .collect(Collectors.toList());
        });

        when(drinkDataReaderService.readStylesToDeleteWithReplacement(anyCollection()))
                .thenAnswer(invocationOnMock -> {
                    var stylesRead = realDrinkDataReaderService.readStylesToDeleteWithReplacement(
                            (Collection<Row>) invocationOnMock.getArgument(0, Collection.class));
                    return stylesRead.entrySet().stream()
                            .collect(Collectors.toMap(
                                    longIdOrNameDtoEntry -> translateIdFromImport(
                                            longIdOrNameDtoEntry.getKey(),
                                            allStylesIdByOldName,
                                            ImporterTestHelper.STYLES_OLD_NAME_BY_ID_FROM_IMPORT),
                                    longIdOrNameDtoEntry -> translateStyleIdFromImport(
                                            longIdOrNameDtoEntry.getValue(),
                                            allStylesIdByOldName)));
                });

        when(drinkDataReaderService.getStyleRows(any(Workbook.class)))
                .thenAnswer(invocation -> realDrinkDataReaderService
                        .getStyleRows(invocation.getArgument(0, Workbook.class)));

    }

    /**
     * Mocks the reading of colors from the drink data file by replacing the color ids from example file with the ids
     * of the existing colors in the DB
     */
    private void mockColorReading(DrinkDataReaderService realDrinkDataReaderService) {
        var allColorsIdByOldName = colorRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Color::getName,
                        Color::getId));

        when(drinkDataReaderService.readColors(anyCollection())).thenAnswer(invocationOnMock -> {
            var colorsRead = realDrinkDataReaderService.readColors((Collection<Row>) invocationOnMock.getArgument(0, Collection.class));
            return colorsRead.stream()
                    .map(color -> color.setId(translateIdFromImport(color.getId(), allColorsIdByOldName, ImporterTestHelper.COLORS_OLD_NAME_BY_ID_FROM_IMPORT)))
                    .collect(Collectors.toList());
        });

        when(drinkDataReaderService.getColorRows(any(Workbook.class)))
                .thenAnswer(invocation -> realDrinkDataReaderService
                        .getColorRows(invocation.getArgument(0, Workbook.class)));

        when(drinkDataReaderService.readColorsToDeleteWithReplacement(anyCollection()))
                .thenAnswer(invocation -> realDrinkDataReaderService
                        .readColorsToDeleteWithReplacement((Collection<Row>) invocation.getArgument(0, Collection.class)));
    }

    private IdOrNameDto translateStyleIdFromImport(IdOrNameDto colorIdFromImport, Map<String, Long> allColorsIdByName) {
        return IdOrNameDto.builder()
                .id(Optional.of(colorIdFromImport)
                            .map(IdOrNameDto::getId)
                            .map(id -> allColorsIdByName.get(ImporterTestHelper.STYLES_OLD_NAME_BY_ID_FROM_IMPORT.get(id)))
                            .orElse(null))
                .name(colorIdFromImport.getName())
                .build();
    }

    private Long translateIdFromImport(Long colorIdFromImport, Map<String, Long> allColorsIdByName, Map<Long, String> oldNameByIdDictionary) {
        if (null == colorIdFromImport) {
            //New color
            return null;
        }
        return allColorsIdByName.get(oldNameByIdDictionary.get(colorIdFromImport));
    }

    private void postFile(String urlTemplate, MockMultipartFile file) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(urlTemplate)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }
}
