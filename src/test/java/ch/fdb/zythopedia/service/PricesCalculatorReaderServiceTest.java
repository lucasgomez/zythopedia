package ch.fdb.zythopedia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PricesCalculatorReaderServiceTest {

    private PricesCalculatorReaderService pricesCalculatorReaderService;

    @BeforeEach
    void setUp() {
        pricesCalculatorReaderService = new PricesCalculatorReaderService();
    }

    @Test
    void readServices() {
        var prices = pricesCalculatorReaderService.readServices(ImporterTestHelper.openXLSXFromResources(ImporterTestHelper.PRICE_CALCULATOR_XLSX));

        assertEquals(5, prices.size());

        var tap = prices.stream()
                .filter(service -> 1477 == service.getId())
                .findFirst()
                .orElseThrow();

        assertEquals(8.0, tap.getSellingPrice());
    }
}
