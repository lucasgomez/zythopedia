package ch.fdb.zythopedia.utils;

import ch.fdb.zythopedia.enums.Strength;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.mapstruct.ap.internal.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public final class SpreadsheetHelper {

    private SpreadsheetHelper() {
    }

    public static Workbook getWorkbookFromFile(MultipartFile multipartFile) {
        var file = FileHelper.toFile(multipartFile);

        try (var inputStream = new FileInputStream(file)) {
            return WorkbookFactory.create(inputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            log.error("File not found", fileNotFoundException);
            throw new IllegalArgumentException("File not found", fileNotFoundException);
        } catch (IOException e) {
            log.error("Could not read file", e);
            throw new IllegalArgumentException("Could not read file", e);
        }
    }

    public static boolean rowHasContent(Sheet sheet, int rowNum) {
        return rowHasContent(sheet, rowNum, 0) || rowHasContent(sheet, rowNum, 1);
    }

    private static boolean rowHasContent(Sheet sheet, int rowNum, int cellNum) {
        return Optional.ofNullable(sheet.getRow(rowNum))
                .map(row -> row.getCell(0))
                .filter(SpreadsheetHelper::hasContent)
                .isPresent()
        || Optional.ofNullable(sheet.getRow(rowNum))
                .map(row -> row.getCell(1))
                .filter(SpreadsheetHelper::hasContent)
                .isPresent();
    }

    public static boolean hasContent(Cell cell) {
        if (null == cell) {
            return false;
        }
        switch (cell.getCellType()) {
            case STRING:
                return Strings.isNotEmpty(cell.getStringCellValue());
            case NUMERIC:
                return true;
            case _NONE:
                return false;
            default:
                throw new IllegalArgumentException(String.format("Too lazy to care about %s", cell.getCellType()));
        }
    }

    public static Collection<Row> readRowsFromSheet(Sheet sheet) {
        var rowNum = 1;
        var rows = (Collection<Row>) new ArrayList<Row>();
        while (rowHasContent(sheet, rowNum)) {
            rows.add(sheet.getRow(rowNum));
            rowNum++;
        }
        return rows;
    }

    public static String getCellStringContent(Row row, int column_num) {
        return Optional.ofNullable(row.getCell(column_num))
                .map(Cell::getStringCellValue)
                .orElse(null);
    }

    public static Double getCellDoubleContent(Row row, int column_num) {
        return Optional.ofNullable(row.getCell(column_num))
                .filter(cell -> CellType.NUMERIC == cell.getCellType())
                .map(Cell::getNumericCellValue)
                .orElse(null);
    }

    public static Long getCellLongContent(Row row, int column_num) {
        return Optional.ofNullable(row.getCell(column_num))
                .map(SpreadsheetHelper::getCellLongContent)
                .orElse(null);
    }

    private static Long getCellLongContent(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC: return ((Double) cell.getNumericCellValue()).longValue();
            case STRING: return Long.valueOf(cell.getStringCellValue());
            default: {
                log.error(String.format("Could not read value from cell %s, thus ignoring it", cell.getAddress().formatAsString()));
                return null;
            }
        }
    }

    public static Strength getCellContentAsStrength(Row row, int column_num) {
        return Optional.ofNullable(row.getCell(column_num))
                .map(Cell::getNumericCellValue)
                .filter(Predicate.not(Objects::isNull))
                .map(Double::longValue)
                .map(Strength::getStrengthByRank)
                .orElse(null);
    }
}
