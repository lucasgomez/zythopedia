package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.enums.ServiceMethod;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.fdb.zythopedia.utils.SpreadsheetHelper.*;

@Service
public class OrderReaderService {

    public final static int CODE_COLUMN_NUM = 0;
    public final static int NAME_COLUMN_NUM = 1;
    public final static int BREWERY_NAME_COLUMN_NUM = 2;
    public final static int STYLE_NAME_COLUMN_NUM = 3;
    public final static int ABV_COLUMN_NUM = 4;
    public final static int VOLUME_IN_CL_COLUMN_NUM = 5;
    public final static int PRODUCER_ORIGIN_COLUMN_NAME = 6;
    public final static int BUYING_PRICE_COLUMN_NUM = 7;
    public final static int SERVICE_METHOD_COLUMN_NUM = 8;

    public List<CreateBoughtDrinkDto> readDrinksOrders(Workbook workbook) {
        var sheet = workbook.getSheetAt(0);
        var rows = readRowsFromSheet(sheet);
        return rows.stream()
                .map(this::buildOrderedDrink)
                .collect(Collectors.toList());
    }

    private CreateBoughtDrinkDto buildOrderedDrink(Row row) {
        return CreateBoughtDrinkDto.builder()
                .code(getCellStringContent(row, CODE_COLUMN_NUM))
                .name(getCellStringContent(row, NAME_COLUMN_NUM))
                .producerName(getCellStringContent(row, BREWERY_NAME_COLUMN_NUM))
                .producerOriginName(getCellStringContent(row, PRODUCER_ORIGIN_COLUMN_NAME))
                .serviceMethod(getServiceMethod(row))
                .styleName(getCellStringContent(row, STYLE_NAME_COLUMN_NUM))
                .abv(getCellDoubleContent(row, ABV_COLUMN_NUM))
                .buyingPrice(getCellDoubleContent(row, BUYING_PRICE_COLUMN_NUM))
                .volumeInCl(getCellLongContent(row, VOLUME_IN_CL_COLUMN_NUM))
                .build();
    }

    private ServiceMethod getServiceMethod(Row row) {
        return Optional.ofNullable(row.getCell(SERVICE_METHOD_COLUMN_NUM))
                .map(Cell::getStringCellValue)
                .map(ServiceMethod::getServiceMethod)
                .orElse(null);
    }
}
