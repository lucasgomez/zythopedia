package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.*;
import ch.fdb.zythopedia.utils.SpreadsheetHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.fdb.zythopedia.utils.SpreadsheetHelper.*;

@Slf4j
@Service
public class DrinkDataReaderService {

    public static final int COLOR_ID_COLUMN_NUM = 0;
    public static final int COLOR_NAME_COLUMN_NUM = 1;
    public static final int COLOR_DESCRIPTION_COLUMN_NUM = 2;
    public static final int COLOR_TO_DELETE_COLUMN_NUM = 3;
    public static final int COLOR_REPLACE_BY_COLUMN_NUM = 4;
    public static final int COLOR_REPLACE_BY_NAME_COLUMN_NUM = 5;

    public static final int ORIGIN_ID_COLUMN_NUM = 0;
    public static final int ORIGIN_NAME_COLUMN_NUM = 1;
    public static final int ORIGIN_SHORT_NAME_COLUMN_NUM = 2;
    public static final int ORIGIN_FLAG_COLUMN_NUM = 3;
    public static final int ORIGIN_TO_DELETE_COLUMN_NUM = 4;
    public static final int ORIGIN_REPLACE_BY_COLUMN_NUM = 5;
    public static final int ORIGIN_REPLACE_BY_NAME_COLUMN_NUM = 6;

    public static final int PRODUCER_ID_COLUMN_NUM = 0;
    public static final int PRODUCER_NAME_COLUMN_NUM = 1;
    public static final int PRODUCER_ORIGIN_ID_COLUMN_NUM = 2;
    public static final int PRODUCER_ORIGIN_NAME_COLUMN_NUM = 3;
    public static final int PRODUCER_TO_DELETE_COLUMN_NUM = 4;
    public static final int PRODUCER_REPLACE_BY_COLUMN_NUM = 5;
    public static final int PRODUCER_REPLACE_BY_NAME_COLUMN_NUM = 6;

    public static final int STYLE_ID_COLUMN_NUM = 0;
    public static final int STYLE_NAME_COLUMN_NUM = 1;
    public static final int STYLE_DESCRIPTION_COLUMN_NUM = 2;
    public static final int STYLE_PARENT_ID_COLUMN_NUM = 3;
    public static final int STYLE_PARENT_NAME_COLUMN_NUM = 4;
    public static final int STYLE_TO_DELETE_COLUMN_NUM = 5;
    public static final int STYLE_REPLACE_BY_COLUMN_NUM = 6;
    public static final int STYLE_REPLACE_BY_NAME_COLUMN_NUM = 7;

    public static final int DRINK_ID_COLUMN_NUM = 0;
    public static final int DRINK_NAME_COLUMN_NUM = 1;
    public static final int DRINK_PRODUCER_ID_COLUMN_NUM = 2;
    public static final int DRINK_PRODUCER_NAME_COLUMN_NUM = 3;
    public static final int DRINK_DESCRIPTION_COLUMN_NUM = 4;
    public static final int DRINK_ABV_COLUMN_NUM = 5;
    public static final int DRINK_COLOR_ID_COLUMN_NUM = 6;
    public static final int DRINK_COLOR_NAME_COLUMN_NUM = 7;
    public static final int DRINK_STYLE_ID_COLUMN_NUM = 8;
    public static final int DRINK_STYLE_NAME_COLUMN_NUM = 9;
    public static final int DRINK_TO_DELETE_COLUMN_NUM = 10;

    public Collection<DrinkDto> readDrinks(Collection<Row> rows) {
        return rows.stream()
                .filter(row -> Strings.isBlank(getCellStringContent(row, DRINK_TO_DELETE_COLUMN_NUM)))
                .map(this::buildDrinkDto)
                .collect(Collectors.toSet());
    }

    public Collection<ColorDto> readColors(Collection<Row> rows) {
        return rows.stream()
                .filter(row -> Strings.isBlank(getCellStringContent(row, COLOR_TO_DELETE_COLUMN_NUM)))
                .map(this::buildColorDto)
                .collect(Collectors.toSet());
    }

    public Collection<OriginDto> readOrigins(Collection<Row> rows) {
        return rows.stream()
                .filter(row -> Strings.isBlank(getCellStringContent(row, ORIGIN_TO_DELETE_COLUMN_NUM)))
                .map(this::buildOriginDto)
                .collect(Collectors.toSet());
    }

    public Collection<ProducerDto> readProducers(Collection<Row> rows) {
        return rows.stream()
                .filter(row -> Strings.isBlank(getCellStringContent(row, PRODUCER_TO_DELETE_COLUMN_NUM)))
                .map(this::buildProducerDto)
                .collect(Collectors.toSet());
    }

    public Collection<StyleDto> readStyles(Collection<Row> rows) {
        return rows.stream()
                .filter(row -> Strings.isBlank(getCellStringContent(row, STYLE_TO_DELETE_COLUMN_NUM)))
                .map(this::buildStyleDto)
                .collect(Collectors.toSet());
    }

    public Collection<Row> getDrinkRows(Workbook workbook) {
        var sheet = workbook.getSheet("drinks");
        return SpreadsheetHelper.readRowsFromSheet(sheet);
    }

    public Collection<Row> getColorRows(Workbook workbook) {
        var sheet = workbook.getSheet("colors");
        return SpreadsheetHelper.readRowsFromSheet(sheet);
    }

    public Collection<Row> getOriginRows(Workbook workbook) {
        var sheet = workbook.getSheet("origins");
        return SpreadsheetHelper.readRowsFromSheet(sheet);
    }

    public Collection<Row> getProducerRows(Workbook workbook) {
        var sheet = workbook.getSheet("producers");
        return SpreadsheetHelper.readRowsFromSheet(sheet);
    }

    public Collection<Row> getStyleRows(Workbook workbook) {
        var sheet = workbook.getSheet("styles");
        return SpreadsheetHelper.readRowsFromSheet(sheet);
    }

    public Map<Long, IdOrNameDto> readDrinksToDelete(Collection<Row> rows) {
        return rows.stream()
                .filter(row -> hasCellStringContent(row, DRINK_TO_DELETE_COLUMN_NUM))
                .collect(Collectors.toMap(
                        row -> SpreadsheetHelper.getCellLongContent(row, DRINK_ID_COLUMN_NUM),
                        row -> IdOrNameDto.builder().build()));
    }

    public Map<Long, IdOrNameDto> readColorsToDeleteWithReplacement(Collection<Row> rows) {
        return readEntityToDeleteWithReplacement(rows,
                COLOR_TO_DELETE_COLUMN_NUM, COLOR_ID_COLUMN_NUM, COLOR_REPLACE_BY_COLUMN_NUM, COLOR_REPLACE_BY_NAME_COLUMN_NUM);
    }

    public Map<Long, IdOrNameDto> readStylesToDeleteWithReplacement(Collection<Row> rows) {
        return readEntityToDeleteWithReplacement(rows,
                STYLE_TO_DELETE_COLUMN_NUM, STYLE_ID_COLUMN_NUM, STYLE_REPLACE_BY_COLUMN_NUM, STYLE_REPLACE_BY_NAME_COLUMN_NUM);
    }

    public Map<Long, IdOrNameDto> readProducersToDeleteWithReplacement(Collection<Row> rows) {
        return readEntityToDeleteWithReplacement(rows,
                PRODUCER_TO_DELETE_COLUMN_NUM, PRODUCER_ID_COLUMN_NUM, PRODUCER_REPLACE_BY_COLUMN_NUM, PRODUCER_REPLACE_BY_NAME_COLUMN_NUM);
    }

    public Map<Long, IdOrNameDto> readOriginsToDeleteWithReplacement(Collection<Row> rows) {
        return readEntityToDeleteWithReplacement(rows,
                ORIGIN_TO_DELETE_COLUMN_NUM, ORIGIN_ID_COLUMN_NUM, ORIGIN_REPLACE_BY_COLUMN_NUM, ORIGIN_REPLACE_BY_NAME_COLUMN_NUM);
    }

    /**
     * @return pairs of id of color to delete (1st) and the optional id of replacement color (2nd)
     */
    private Map<Long, IdOrNameDto> readEntityToDeleteWithReplacement(Collection<Row> rows, int entityToDeleteColumnNum,
                                                                     int entityIdColumnNum, int entityToReplaceByColumnNum,
                                                                     int entityToReplaceByNameColumnNum) {
        return rows.stream()
                .filter(row -> Strings.isNotBlank(getCellStringContent(row, entityToDeleteColumnNum)))
                .collect(Collectors.toMap(
                        row -> SpreadsheetHelper.getCellLongContent(row, entityIdColumnNum),
                        row -> IdOrNameDto.builder()
                                .id(getCellLongContent(row, entityToReplaceByColumnNum))
                                .name(getCellStringContent(row, entityToReplaceByNameColumnNum))
                                .build()
                ));
    }

    private ColorDto buildColorDto(Row row) {
        return ColorDto.builder()
                .id(getCellLongContent(row, COLOR_ID_COLUMN_NUM))
                .name(getCellStringContent(row, COLOR_NAME_COLUMN_NUM))
                .description(getCellStringContent(row, COLOR_DESCRIPTION_COLUMN_NUM))
                .build();
    }

    private OriginDto buildOriginDto(Row row) {
        return OriginDto.builder()
                .id(getCellLongContent(row, ORIGIN_ID_COLUMN_NUM))
                .name(getCellStringContent(row, ORIGIN_NAME_COLUMN_NUM))
                .shortName(getCellStringContent(row, ORIGIN_SHORT_NAME_COLUMN_NUM))
                .flag(getCellStringContent(row, ORIGIN_FLAG_COLUMN_NUM))
                .build();
    }

    private DrinkDto buildDrinkDto(Row row) {
        return DrinkDto.builder()
                .id(getCellLongContent(row, DRINK_ID_COLUMN_NUM))
                .name(getCellStringContent(row, DRINK_NAME_COLUMN_NUM))
                .description(getCellStringContent(row, DRINK_DESCRIPTION_COLUMN_NUM))
                .abv(getCellDoubleContent(row, DRINK_ABV_COLUMN_NUM))
                .color(ColorDto.builder()
                        .id(getCellLongContent(row, DRINK_COLOR_ID_COLUMN_NUM))
                        .name(getCellStringContent(row, DRINK_COLOR_NAME_COLUMN_NUM))
                        .build())
                .style(StyleDto.builder()
                        .id(getCellLongContent(row, DRINK_STYLE_ID_COLUMN_NUM))
                        .name(getCellStringContent(row, DRINK_STYLE_NAME_COLUMN_NUM))
                        .build())
                .producer(ProducerDto.builder()
                        .id(getCellLongContent(row, DRINK_PRODUCER_ID_COLUMN_NUM))
                        .name(getCellStringContent(row, DRINK_PRODUCER_NAME_COLUMN_NUM))
                        .build())
                .build();
    }

    private ProducerDto buildProducerDto(Row row) {
        return ProducerDto.builder()
                .id(getCellLongContent(row, PRODUCER_ID_COLUMN_NUM))
                .name(getCellStringContent(row, PRODUCER_NAME_COLUMN_NUM))
                .origin(OriginDto.builder()
                        .id(getCellLongContent(row, PRODUCER_ORIGIN_ID_COLUMN_NUM))
                        .name(getCellStringContent(row, PRODUCER_ORIGIN_NAME_COLUMN_NUM))
                        .build())
                .build();
    }

    private StyleDto buildStyleDto(Row row) {
        return StyleDto.builder()
                .id(getCellLongContent(row, STYLE_ID_COLUMN_NUM))
                .name(getCellStringContent(row, STYLE_NAME_COLUMN_NUM))
                .description(getCellStringContent(row, STYLE_DESCRIPTION_COLUMN_NUM))
                .parentId(getCellLongContent(row, STYLE_PARENT_ID_COLUMN_NUM))
                .parentName(getCellStringContent(row, STYLE_PARENT_NAME_COLUMN_NUM))
                .build();
    }
}
