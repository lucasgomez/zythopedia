package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.ServiceDto;
import ch.fdb.zythopedia.utils.SpreadsheetHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static ch.fdb.zythopedia.utils.SpreadsheetHelper.*;

@Slf4j
@Service
public class PricesCalculatorReaderService {

    public static final int PRICE_CALCULATOR_SERVICE_ID_COLUMN_NUM = 0;
    public static final int PRICE_CALCULATOR_PRICE_COLUMN_NUM = 11;
    public static final int PRICE_CALCULATOR_PRODUCER_NAME_COLUMN_NUM = 2;
    public static final int PRICE_CALCULATOR_DRINK_NAME_COLUMN_NUM = 4;

    public Collection<ServiceDto> readServices(Workbook workbook) {
        var sheet = workbook.getSheetAt(0);
        var rows = SpreadsheetHelper.readRowsFromSheet(sheet);
        return rows.stream()
                .map(this::buildServiceDto)
                .filter(dto -> Objects.nonNull(dto.getId()))
                .filter(dto -> Objects.nonNull(dto.getSellingPrice()))
                .collect(Collectors.toList());
    }

    private ServiceDto buildServiceDto(Row row) {
        return ServiceDto.builder()
                .id(getCellLongContent(row, PRICE_CALCULATOR_SERVICE_ID_COLUMN_NUM))
                .sellingPrice(getCellDoubleContent(row, PRICE_CALCULATOR_PRICE_COLUMN_NUM))
                .drinkName(getCellStringContent(row, PRICE_CALCULATOR_DRINK_NAME_COLUMN_NUM))
                .producerName(getCellStringContent(row, PRICE_CALCULATOR_PRODUCER_NAME_COLUMN_NUM))
                .build();
    }
}
