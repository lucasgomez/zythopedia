package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.dto.DrinkDto;
import ch.fdb.zythopedia.dto.ProducerDto;
import ch.fdb.zythopedia.dto.StyleDto;
import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.dto.creation.CreateColorDto;
import ch.fdb.zythopedia.dto.creation.CreateProducerDto;
import ch.fdb.zythopedia.dto.creation.CreateStyleDto;
import ch.fdb.zythopedia.enums.ServiceMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ch.fdb.zythopedia.utils.SpreadsheetHelper.*;

@Slf4j
@Service
public class AmsteinReaderService {

    private static final int ORDER_CODE_COLUMN_NUM = 1;
    private static final int ORDER_NAME_COLUMN_NUM = 2;
    private static final int ORDER_BUYING_PRICE_COLUMN_NUM = 5;
    private static final int ORDER_CONTENT_TYPE_COLUMN_NUM = 4;

    private static final int CATALOG_CODE_COLUMN_NUM = 0;
    private static final int CATALOG_NAME_COLUMN_NUM = 1;
    private static final int CATALOG_VOLUME_COLUMN_NUM = 2;
    private static final int CATALOG_STYLE_COLUMN_NUM = 4;
    private static final int CATALOG_COLOR_COLUMN_NUM = 5;
    private static final int CATALOG_ORIGIN_COLUMN_NUM = 6;
    private static final int CATALOG_ABV_COLUMN_NUM = 7;
    private static final int CATALOG_SOURNESS_COLUMN_NUM = 8;
    private static final int CATALOG_BITTERNESS_COLUMN_NUM = 9;
    private static final int CATALOG_SWEETNESS_COLUMN_NUM = 10;
    private static final int CATALOG_HOPPINESS_COLUMN_NUM = 11;
    private static final int CATALOG_PRODUCER_COLUMN_NUM = 12;

    public Map<CreateBoughtDrinkDto, DrinkDto> readDrinkFromCatalogFile(Sheet sheet) {
        return readRowsFromSheet(sheet).stream()
                .map(this::getDrinkByBoughtDrinkFromRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public Collection<CreateProducerDto> readProducersFromCatalogFile(Sheet sheet) {
        return readRowsFromSheet(sheet).stream()
                .map(this::getProducerFromRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Collection<CreateStyleDto> readStylesFromCatalogFile(Sheet sheet) {
        return readRowsFromSheet(sheet).stream()
                .map(this::getStyleFromRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Collection<CreateColorDto> readColorsFromCatalogFile(Sheet sheet) {
        return readRowsFromSheet(sheet).stream()
                .map(this::getColorFromRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Collection<CreateBoughtDrinkDto> readBoughtDrinkFromOrderFile(Sheet sheet) {
        return readRowsFromSheet(sheet).stream()
                .map(this::getBoughtDrinkFromRow)
                .filter(boughtDrinkDto -> Objects.nonNull(boughtDrinkDto.getServiceMethod()))
                .collect(Collectors.toSet());
    }

    private CreateColorDto getColorFromRow(Row row) {
        return Optional.ofNullable(row.getCell(CATALOG_COLOR_COLUMN_NUM))
                .map(Cell::getStringCellValue)
                .filter(Predicate.not(String::isBlank))
                .map(content -> CreateColorDto.builder().name(content).build())
                .orElse(null);
    }

    private Pair<CreateBoughtDrinkDto, DrinkDto> getDrinkByBoughtDrinkFromRow(Row row) {
        return Optional.ofNullable(row.getCell(CATALOG_CODE_COLUMN_NUM))
                .map(code -> Pair.of(
                        buildBoughtDrinkDtoFromRow(row),
                        buildDrinkDtoFromRow(row)))
                .orElse(null);
    }

    private CreateBoughtDrinkDto buildBoughtDrinkDtoFromRow(Row row) {
        return CreateBoughtDrinkDto.builder()
                .code(getCellStringContent(row, CATALOG_CODE_COLUMN_NUM))
                .volumeInCl(convertLToCl(getCellDoubleContent(row, CATALOG_VOLUME_COLUMN_NUM)))
                .build();
    }

    private Long convertLToCl(Double volumeInL) {
        return Double.valueOf(volumeInL * 100).longValue();
    }

    private DrinkDto buildDrinkDtoFromRow(Row row) {
        return DrinkDto.builder()
                .name(getCellStringContent(row, CATALOG_NAME_COLUMN_NUM))
                .abv(getCellDoubleContent(row, CATALOG_ABV_COLUMN_NUM))
                .color(ColorDto.builder()
                        .name(getCellStringContent(row, CATALOG_COLOR_COLUMN_NUM))
                        .build())
                .style(StyleDto.builder()
                        .name(getCellStringContent(row, CATALOG_STYLE_COLUMN_NUM))
                        .build())
                .producer(ProducerDto.builder()
                        .name(getCellStringContent(row, CATALOG_PRODUCER_COLUMN_NUM))
                        .build())
                .sourness(getCellContentAsStrength(row, CATALOG_SOURNESS_COLUMN_NUM))
                .bitterness(getCellContentAsStrength(row, CATALOG_BITTERNESS_COLUMN_NUM))
                .sweetness(getCellContentAsStrength(row, CATALOG_SWEETNESS_COLUMN_NUM))
                .hoppiness(getCellContentAsStrength(row, CATALOG_HOPPINESS_COLUMN_NUM))
                .build();
    }

    private CreateProducerDto getProducerFromRow(Row row) {
        return Optional.ofNullable(row.getCell(CATALOG_PRODUCER_COLUMN_NUM))
                .map(Cell::getStringCellValue)
                .filter(Predicate.not(String::isBlank))
                .map(content -> CreateProducerDto.builder()
                        .name(content)
                        .originName(getCellStringContent(row, CATALOG_ORIGIN_COLUMN_NUM))
                        .build())
                .orElse(null);
    }

    private CreateStyleDto getStyleFromRow(Row row) {
        return Optional.ofNullable(row.getCell(CATALOG_STYLE_COLUMN_NUM))
                .map(Cell::getStringCellValue)
                .filter(Predicate.not(String::isBlank))
                .map(content -> CreateStyleDto.builder().name(content).build())
                .orElse(null);
    }

    private CreateBoughtDrinkDto getBoughtDrinkFromRow(org.apache.poi.ss.usermodel.Row row) {
        return CreateBoughtDrinkDto.builder()
                .code(getCellStringContent(row, ORDER_CODE_COLUMN_NUM))
                .name(getCellStringContent(row, ORDER_NAME_COLUMN_NUM))
                .buyingPrice(getCellDoubleContent(row, ORDER_BUYING_PRICE_COLUMN_NUM))
                .serviceMethod(getServiceMethodFromAmsteinContentType(getCellStringContent(row, ORDER_CONTENT_TYPE_COLUMN_NUM)))
                .returnable(isReturnableFromAmsteinContentType(getCellStringContent(row, ORDER_CONTENT_TYPE_COLUMN_NUM)))
                .build();
    }

    private ServiceMethod getServiceMethodFromAmsteinContentType(String contentType) {
        switch (contentType) {
            case "FUT":
                return ServiceMethod.TAP;
            case "CAR":
            case "CAI":
                return ServiceMethod.BOTTLE;
            default:
                return null;
        }
    }

    private boolean isReturnableFromAmsteinContentType(String contentType) {
        return "CAI".equalsIgnoreCase(contentType);
    }
}
