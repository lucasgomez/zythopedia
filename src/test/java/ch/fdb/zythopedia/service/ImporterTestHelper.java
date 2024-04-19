package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.utils.SpreadsheetHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Map;

public class ImporterTestHelper {

    public static final String AMSTEIN_CATALOG_XLSX = "amstein_catalog.xlsx";
    public static final String AMSTEIN_ORDER_XLSX = "amstein_import.xlsx";
    public static final String PRICE_CALCULATOR_XLSX = "priceCalculator.xlsx";
    public static final String DRINK_DATA_XLSX = "drinkData.xlsx";
    public static final Map<Long, String> COLORS_OLD_NAME_BY_ID_FROM_IMPORT = Map.of(
            1564L, "Aromatisée",
            1563L, "Blonde",
            1565L, "Brune",
            1562L, "Fruitée",
            1566L, "Noir",
            1561L, "Rousse / Ambrée"
    );

    public static final Map<Long, String> STYLES_OLD_NAME_BY_ID_FROM_IMPORT = Map.of(
            1567L, "Lambic/Bière acide/Berliner Weisse",
            1574L, "Bière de froment / Blanche / Weissbier",
            1576L, "COCA-COLA HBC SUISSE SA",
            1582L, "NESTLE WATERS (SUISSE) SA",
            1583L, "RED BULL (SUISSE) SA",
            1585L, "India Pale Ale",
            1588L, "Lager / Premium"
    );
    public static final Map<Long, String> PRODUCERS_OLD_NAME_BY_ID_FROM_IMPORT = Map.of(
            1631L, "Stone Brewing",
            1610L, "Stone Brewing  USA",
            1626L, "CRAK Brewery",
            1617L, "Beavertown Brewery",
            1595L, "Boon"
    );

    public static final Map<Long, String> ORIGINS_OLD_NAME_BY_ID_FROM_IMPORT = Map.of(
            1616L, "Angleterre",
            1590L, "Corse",
            1600L, "Ecosse",
            1609L, "Etats-Unis"
    );

    public static MockMultipartFile openFileFromResources(String fileName) {
        try (var file = ImporterTestHelper.class.getResourceAsStream(String.format("/%s", fileName))) {
            return new MockMultipartFile(
                    "file",
                    fileName,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    file);
        } catch (IOException e) {
            Assertions.fail("Could not open file");
            throw new RuntimeException(e);
        }
    }

    public static Workbook openXLSXFromResources(String fileName) {
        var file = ImporterTestHelper.openFileFromResources(fileName);
        return SpreadsheetHelper.getWorkbookFromFile(file);
    }

    public static Sheet openXLSXFromResources(String fileName, int sheetNumber) {
        return ImporterTestHelper.openXLSXFromResources(fileName).getSheetAt(sheetNumber);
    }

}
