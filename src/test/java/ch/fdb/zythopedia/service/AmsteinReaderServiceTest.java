package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.enums.Strength;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AmsteinReaderServiceTest {

    public static final String CRAK_CODE = "CRANZ040BO";
    private AmsteinReaderService amsteinReaderService;

    @BeforeEach
    void setUp() {
        amsteinReaderService = new AmsteinReaderService();
    }

    @Test
    void testReadCatalogFile() {
        var sheet = getCatalogSheet();
        var drinks = amsteinReaderService.readDrinkFromCatalogFile(sheet);

        assertEquals(68L, drinks.size());
        var foundDrink = drinks.entrySet().stream()
                .filter(drink -> CRAK_CODE.equals(drink.getKey().getCode()))
                .findFirst()
                .orElseThrow();
        var drinkDto = foundDrink.getValue();
        var boughtDrinkDto = foundDrink.getKey();

        assertEquals("CR/AK New Zealand", drinkDto.getName());
        assertEquals("CRAK Brewery", drinkDto.getProducer().getName());
        assertEquals("India Pale Ale", drinkDto.getStyle().getName());
        assertEquals("Blonde", drinkDto.getColor().getName());
        assertEquals(7.0, drinkDto.getAbv());
        assertEquals(Strength.LOW, drinkDto.getSourness());
        assertEquals(Strength.MILD, drinkDto.getBitterness());
        assertEquals(Strength.LOW, drinkDto.getSweetness());
        assertEquals(Strength.STRONG, drinkDto.getHoppiness());

        assertEquals(CRAK_CODE, boughtDrinkDto.getCode());
        assertEquals(40, boughtDrinkDto.getVolumeInCl());
    }

    private static Sheet getCatalogSheet() {
        return ImporterTestHelper.openXLSXFromResources(ImporterTestHelper.AMSTEIN_CATALOG_XLSX, 0);
    }

    @Test
    void testReadProducers() {
        var sheet = getCatalogSheet();
        var producers = amsteinReaderService.readProducersFromCatalogFile(sheet);

        assertEquals(37L, producers.size());
        var producerDto = producers.stream()
                .filter(producer -> "CRAK Brewery".equals(producer.getName()))
                .findFirst()
                .orElseThrow();

        assertEquals("CRAK Brewery", producerDto.getName());
        assertEquals("Italie", producerDto.getOriginName());
    }

    @Test
    void testReadStyles() {
        var sheet = getCatalogSheet();
        var styles = amsteinReaderService.readStylesFromCatalogFile(sheet);

        assertEquals(23L, styles.size());
        var styleDto = styles.stream()
                .filter(style -> "India Pale Ale".equals(style.getName()))
                .findFirst()
                .orElseThrow();

        assertEquals("India Pale Ale", styleDto.getName());
    }

    @Test
    void testReadColors() {
        var sheet = getCatalogSheet();
        var colors = amsteinReaderService.readColorsFromCatalogFile(sheet);

        assertEquals(6L, colors.size());
        var colorDto = colors.stream()
                .filter(color -> "Blonde".equals(color.getName()))
                .findFirst()
                .orElseThrow();

        assertEquals("Blonde", colorDto.getName());
    }

    @Test
    void testReadBoughtDrinks() {
        var sheet = ImporterTestHelper.openXLSXFromResources(ImporterTestHelper.AMSTEIN_ORDER_XLSX, 0);
        var boughtDrinks = amsteinReaderService.readBoughtDrinkFromOrderFile(sheet);

        assertEquals(68L, boughtDrinks.size());
        var boughtDrinkDto = boughtDrinks.stream()
                .filter(boughtDrink -> CRAK_CODE.equals(boughtDrink.getCode()))
                .findFirst()
                .orElseThrow();

        assertEquals(CRAK_CODE, boughtDrinkDto.getCode());
        assertEquals(4.13, boughtDrinkDto.getBuyingPrice());
        assertEquals(ServiceMethod.BOTTLE, boughtDrinkDto.getServiceMethod());
        assertFalse(boughtDrinkDto.getReturnable());
    }

}
