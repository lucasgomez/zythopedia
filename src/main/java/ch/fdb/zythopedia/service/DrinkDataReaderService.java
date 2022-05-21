package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.ColorDto;
import ch.fdb.zythopedia.utils.SpreadsheetHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.SHA256Digest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.fdb.zythopedia.utils.SpreadsheetHelper.*;

@Slf4j
@Service
public class DrinkDataReaderService {

    public static final int COLOR_ID_COLUMN_NUM = 0;
    public static final int TO_DELETE_COLUMN_NUM = 3;
    public static final int REPLACE_BY_COLUMN_NUM = 4;

    public Collection<ColorDto> readColorsToUpdate(Collection<Row> rows) {
        return rows.stream()
                .filter(row -> Strings.isBlank(getCellStringContent(row, 3)))
                .map(this::buildColorDto)
                .collect(Collectors.toSet());
    }

    public Collection<Row> getColorRows(Workbook workbook) {
        var sheet = workbook.getSheet("colors");
        return SpreadsheetHelper.readRowsFromSheet(sheet);
    }

    /**
     * @return pairs of id of color to delete (1st) and the optional id of replacement color (2nd)
     */
    public Map<Long, Long> readColorsToDeleteWithReplacement(Collection<Row> rows) {
        return rows.stream()
                .filter(row -> Strings.isNotBlank(getCellStringContent(row, TO_DELETE_COLUMN_NUM)))
                .collect(Collectors.toMap(
                        row -> SpreadsheetHelper.getCellLongContent(row, COLOR_ID_COLUMN_NUM),
                        row -> getCellLongContent(row, REPLACE_BY_COLUMN_NUM)
                ));
    }

    private ColorDto buildColorDto(Row row) {
        return ColorDto.builder()
                .id(getCellLongContent(row, 0))
                .name(getCellStringContent(row, 1))
                .description(getCellStringContent(row, 2))
                .build();
    }
}
