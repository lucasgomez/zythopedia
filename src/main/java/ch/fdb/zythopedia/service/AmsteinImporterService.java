package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.*;
import ch.fdb.zythopedia.enums.ServiceMethod;
import ch.fdb.zythopedia.enums.Strength;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmsteinImporterService {

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

    public Map<CreateBoughtDrinkDto, CreateDrinkDto> readDrinkFromCatalogFile(MultipartFile multipartFile) {
        return readRowsFromFile(multipartFile).stream()
                .map(this::getDrinkByBoughtDrinkFromRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public Collection<CreateProducerDto> readProducersFromCatalogFile(MultipartFile multipartFile) {
        return readRowsFromFile(multipartFile).stream()
                .map(this::getProducerFromRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Collection<CreateStyleDto> readStylesFromCatalogFile(MultipartFile multipartFile) {
        return readRowsFromFile(multipartFile).stream()
                .map(this::getStyleFromRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Collection<CreateColorDto> readColorsFromCatalogFile(MultipartFile multipartFile) {
        return readRowsFromFile(multipartFile)
                .stream()
                .map(this::getColorFromRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Collection<CreateBoughtDrinkDto> readBoughtDrinkFromOrderFile(MultipartFile multipartFile) {
        return readRowsFromFile(multipartFile)
                .stream()
                .map(this::getBoughtDrinkFromRow)
                .collect(Collectors.toSet());
    }

    private CreateColorDto getColorFromRow(Row row) {
        return Optional.ofNullable(row.getCell(CATALOG_COLOR_COLUMN_NUM))
                .map(Cell::getStringCellValue)
                .filter(Predicate.not(String::isBlank))
                .map(content -> CreateColorDto.builder().name(content).build())
                .orElse(null);
    }

    private Pair<CreateBoughtDrinkDto, CreateDrinkDto> getDrinkByBoughtDrinkFromRow(Row row) {
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

    private String getCellStringContent(Row row, int column_num) {
        return Optional.ofNullable(row.getCell(column_num))
                .map(Cell::getStringCellValue)
                .orElse(null);
    }

    private Double getCellDoubleContent(Row row, int column_num) {
        return Optional.ofNullable(row.getCell(column_num))
                .map(Cell::getNumericCellValue)
                .orElse(null);
    }

    private Strength getCellContentAsStrength(Row row, int column_num) {
        return Optional.ofNullable(row.getCell(column_num))
                .map(Cell::getNumericCellValue)
                .filter(Predicate.not(Objects::isNull))
                .map(Double::longValue)
                .map(Strength::getStrengthByRank)
                .orElse(null);
    }

    private CreateDrinkDto buildDrinkDtoFromRow(Row row) {
        return CreateDrinkDto.builder()
                .name(getCellStringContent(row, CATALOG_NAME_COLUMN_NUM))
                .abv(getCellDoubleContent(row, CATALOG_ABV_COLUMN_NUM))
                .colorName(getCellStringContent(row, CATALOG_COLOR_COLUMN_NUM))
                .styleName(getCellStringContent(row, CATALOG_STYLE_COLUMN_NUM))
                .producerName(getCellStringContent(row, CATALOG_PRODUCER_COLUMN_NUM))
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
                .build();
    }

    private ServiceMethod getServiceMethodFromAmsteinContentType(String contentType) {
        switch (contentType) {
            case "FUT": return ServiceMethod.TAP;
            case "CAR": return ServiceMethod.BOTTLE;
            default: return null;
        }
    }

    private boolean rowHasContent(Sheet sheet, int rowNum) {
        return Optional.ofNullable(sheet.getRow(rowNum))
                .map(row -> row.getCell(ORDER_CODE_COLUMN_NUM))
                .map(Cell::getStringCellValue)
                .map(content -> !content.isBlank())
                .orElse(false);
    }

    private static File toFile(MultipartFile multipartFile) {
        var fileToSave = new File(String.format(
                "tmp/%s_%s",
                UUID.randomUUID(),
                multipartFile.getOriginalFilename()));

        try (var outputStream = new FileOutputStream(fileToSave)) {
            if (0 == multipartFile.getBytes().length) {
                throw new RuntimeException(String.format("File %s is empty", multipartFile.getOriginalFilename()));
            }
            outputStream.write(multipartFile.getBytes());
        } catch (IOException ex) {
            throw new RuntimeException(String.format("File %s could not be read", multipartFile.getOriginalFilename()));
        }

        return fileToSave;
    }

    private Collection<Row> readRowsFromFile(MultipartFile multipartFile) {
        var file = toFile(multipartFile);

        try (var inputStream = new FileInputStream(file)) {
            var workbook = WorkbookFactory.create(inputStream);
            var sheet = workbook.getSheetAt(0);

            var rowNum = 1;
            var rows = new ArrayList<Row>();
            while (rowHasContent(sheet, rowNum)) {
                rows.add(sheet.getRow(rowNum));
                rowNum++;
            }
            return rows;
        } catch (FileNotFoundException fileNotFoundException) {
            log.error("File not found", fileNotFoundException);
            throw new IllegalArgumentException("File not found", fileNotFoundException);
        } catch (IOException e) {
            log.error("Could not read file", e);
            throw new IllegalArgumentException("Could not read file", e);
        }
    }
}
