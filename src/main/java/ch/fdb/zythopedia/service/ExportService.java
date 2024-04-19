package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.*;
import ch.fdb.zythopedia.dto.mapper.DrinkMapper;
import ch.fdb.zythopedia.dto.mapper.DrinkPriceCalculatorDtoMapper;
import ch.fdb.zythopedia.dto.mapper.SimpleDrinkMapper;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.enums.ServiceMethod;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportService {

    public static final double TAP_COEFFICIENT = 3.0;
    public static final double BOTTLE_COEFFICIENT = 2.3;
    public static final List<String> PRICE_CALCULATOR_HEADERS = List.of("id", "boughtDrinkId", "producerName", "producerOriginName", "name", "colorName",
            "styleName", "abv", "buyingPrice", "serviceMethod", "volumeInCl", "sellingPrice",
            "projectedSellingPrice", "price per alc. cL", "Projected margin", "Absolute margin");
    private DrinkService drinkService;
    private BoughtDrinkService boughtDrinkService;
    private StyleService styleService;
    private ProducerService producerService;
    private OriginService originService;
    private ColorService colorService;
    private DrinkPriceCalculatorDtoMapper drinkPriceCalculatorDtoMapper;
    private DrinkMapper drinkMapper;
    private SimpleDrinkMapper simpleDrinkMapper;

    public ExportService(DrinkService drinkService, BoughtDrinkService boughtDrinkService, StyleService styleService, ProducerService producerService, OriginService originService, ColorService colorService, DrinkPriceCalculatorDtoMapper drinkPriceCalculatorDtoMapper, DrinkMapper drinkMapper, SimpleDrinkMapper simpleDrinkMapper) {
        this.drinkService = drinkService;
        this.boughtDrinkService = boughtDrinkService;
        this.styleService = styleService;
        this.producerService = producerService;
        this.originService = originService;
        this.colorService = colorService;
        this.drinkPriceCalculatorDtoMapper = drinkPriceCalculatorDtoMapper;
        this.drinkMapper = drinkMapper;
        this.simpleDrinkMapper = simpleDrinkMapper;
    }

    @Transactional
    public File getCalculatorForCurrentEdition() {
        var calculatorLines = boughtDrinkService.getCurrentEditionBoughtDrinks().stream()
                .map(BoughtDrink::getServices)
                .flatMap(List::stream)
                .map(drinkPriceCalculatorDtoMapper::toDto)
                .sorted(Comparator.comparing(DrinkPriceCalculatorDto::getBoughtDrinkId, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        var workbook = buildPriceCalculatorWorkbook(calculatorLines);
        return saveWorkbookInTempFolder(workbook, "priceCalculator");
    }

    @Transactional
    public File getDrinksData(String ediionName) {
        var currentEditionDrinks = boughtDrinkService.getBoughtDrinks(ediionName).stream()
                .map(BoughtDrink::getDrink)
                .filter(Objects::nonNull)
                .map(drinkMapper::toDto)
                .collect(Collectors.toSet());
        var drinksWithNoService = drinkService.findDrinksWithNoService().stream()
                .map(drinkMapper::toDto)
                .collect(Collectors.toList());

        var drinksToExport = new ArrayList<DrinkDto>();
        drinksToExport.addAll(currentEditionDrinks);
        drinksToExport.addAll(drinksWithNoService);

        var workbook = buildDataExporterWorkbook(drinksToExport);
        return saveWorkbookInTempFolder(workbook, "drinkData");
    }

    private File saveWorkbookInTempFolder(Workbook workbook, String fileBaseName) {
        var fileName = String.format("tmp/%s_%s.xlsx", UUID.randomUUID(), fileBaseName);
        try (OutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            return new File(fileName);
        } catch (IOException ioException) {
            log.error("Could not save file to export due to some mysterious reason", ioException);
            throw new RuntimeException("Could not save file to export due to some mysterious reason", ioException);
        }
    }

    private Workbook buildDataExporterWorkbook(Collection<DrinkDto> drinks) {
        var workbook = (Workbook) new XSSFWorkbook();
        var boldCellStyle = buildBoldCellStyle(workbook);

        buildDrinksSheet(workbook, drinks, boldCellStyle);
        buildStylesSheet(workbook, styleService.findAllDto(), boldCellStyle);
        buildColorsSheet(workbook, colorService.findAllDto(), boldCellStyle);
        buildProducersSheet(workbook, producerService.findAllDto(), boldCellStyle);
        buildOriginsSheet(workbook, originService.findAllDto(), boldCellStyle);

        return workbook;
    }

    private Sheet buildDrinksSheet(Workbook workbook, Collection<DrinkDto> drinks, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("drinks");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "producerId", "producerName", "description",
                "Abv", "ColorId", "ColorName", "StyleId", "StyleName", "toDelete"));

        var simpleDrinks = drinks.stream()
                .map(simpleDrinkMapper::toSimplerDto)
                .sorted(Comparator.comparing(SimpleDrinkDto::getName))
                .collect(Collectors.toList());

        for (var rowId = 0; rowId < drinks.size(); rowId++) {
            var cellId = 0;
            var row = sheet.createRow(rowId + 1);
            var drink = simpleDrinks.get(rowId);

            writeContentToCell(row, DrinkDataReaderService.DRINK_ID_COLUMN_NUM, drink.getId());
            writeContentToCell(row, DrinkDataReaderService.DRINK_NAME_COLUMN_NUM, drink.getName());
            writeContentToCell(row, DrinkDataReaderService.DRINK_PRODUCER_ID_COLUMN_NUM, drink.getProducerId());
            writeContentToCell(row, DrinkDataReaderService.DRINK_PRODUCER_NAME_COLUMN_NUM, drink.getProducerName());
            writeContentToCell(row, DrinkDataReaderService.DRINK_DESCRIPTION_COLUMN_NUM, drink.getDescription());
            writeContentToCell(row, DrinkDataReaderService.DRINK_ABV_COLUMN_NUM, drink.getAbv());
            writeContentToCell(row, DrinkDataReaderService.DRINK_COLOR_ID_COLUMN_NUM, drink.getColorId());
            writeContentToCell(row, DrinkDataReaderService.DRINK_COLOR_NAME_COLUMN_NUM, drink.getColorName());
            writeContentToCell(row, DrinkDataReaderService.DRINK_STYLE_ID_COLUMN_NUM, drink.getStyleId());
            writeContentToCell(row, DrinkDataReaderService.DRINK_STYLE_NAME_COLUMN_NUM, drink.getStyleName());
        }
        return sheet;
    }

    private Sheet buildOriginsSheet(Workbook workbook, List<OriginDto> origins, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("origins");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "shortName", "flag", "toDelete", "replaceById", "replaceByName"));

        for (var rowId = 0; rowId < origins.size(); rowId++) {
            var row = sheet.createRow(rowId + 1);
            var origin = origins.get(rowId);

            writeContentToCell(row, DrinkDataReaderService.ORIGIN_ID_COLUMN_NUM, origin.getId());
            writeContentToCell(row, DrinkDataReaderService.ORIGIN_NAME_COLUMN_NUM, origin.getName());
            writeContentToCell(row, DrinkDataReaderService.ORIGIN_SHORT_NAME_COLUMN_NUM, origin.getShortName());
            writeContentToCell(row, DrinkDataReaderService.ORIGIN_FLAG_COLUMN_NUM, origin.getFlag());
        }
        return sheet;
    }

    private Sheet buildProducersSheet(Workbook workbook, List<ProducerDto> producers, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("producers");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "originId", "originName", "toDelete", "replaceById", "replaceByName"));

        for (var rowId = 0; rowId < producers.size(); rowId++) {
            var row = sheet.createRow(rowId + 1);
            var producer = producers.get(rowId);

            var optionalOrigin = Optional.ofNullable(producer.getOrigin());

            writeContentToCell(row, DrinkDataReaderService.PRODUCER_ID_COLUMN_NUM, producer.getId());
            writeContentToCell(row, DrinkDataReaderService.PRODUCER_NAME_COLUMN_NUM, producer.getName());
            writeContentToCell(row, DrinkDataReaderService.PRODUCER_ORIGIN_ID_COLUMN_NUM, optionalOrigin
                    .map(OriginDto::getId)
                    .map(String::valueOf)
                    .orElse(""));
            writeContentToCell(row, DrinkDataReaderService.PRODUCER_ORIGIN_NAME_COLUMN_NUM, optionalOrigin
                    .map(OriginDto::getName)
                    .orElse(""));
        }
        return sheet;
    }

    private Sheet buildStylesSheet(Workbook workbook, List<StyleDto> styles, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("styles");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "description", "parentId", "parentName", "toDelete", "replaceById", "replaceByName"));

        for (var rowId = 0; rowId < styles.size(); rowId++) {
            var row = sheet.createRow(rowId + 1);
            var style = styles.get(rowId);

            writeContentToCell(row, DrinkDataReaderService.STYLE_ID_COLUMN_NUM, style.getId());
            writeContentToCell(row, DrinkDataReaderService.STYLE_NAME_COLUMN_NUM, style.getName());
            writeContentToCell(row, DrinkDataReaderService.STYLE_DESCRIPTION_COLUMN_NUM, style.getDescription());
            writeContentToCell(row, DrinkDataReaderService.STYLE_PARENT_ID_COLUMN_NUM, style.getParentId());
            writeContentToCell(row, DrinkDataReaderService.STYLE_PARENT_NAME_COLUMN_NUM, style.getParentName());
        }
        return sheet;
    }

    private Sheet buildColorsSheet(Workbook workbook, List<ColorDto> colors, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("colors");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "description", "toDelete", "replaceById", "replaceByName"));

        for (var rowId = 0; rowId < colors.size(); rowId++) {
            var row = sheet.createRow(rowId + 1);
            var color = colors.get(rowId);

            writeContentToCell(row, DrinkDataReaderService.COLOR_ID_COLUMN_NUM, color.getId());
            writeContentToCell(row, DrinkDataReaderService.COLOR_NAME_COLUMN_NUM, color.getName());
            writeContentToCell(row, DrinkDataReaderService.COLOR_DESCRIPTION_COLUMN_NUM, color.getDescription());
        }
        return sheet;
    }

    private Workbook buildPriceCalculatorWorkbook(List<DrinkPriceCalculatorDto> dtos) {
        var workbook = (Workbook) new XSSFWorkbook();
        var sheet = workbook.createSheet();

        var boldCellStyle = buildBoldCellStyle(workbook);
        var numberCellStyle = buildRoundedNumberCellStyle(workbook);

        addHeader(sheet, boldCellStyle, PRICE_CALCULATOR_HEADERS);

        for (var rowId = 0; rowId < dtos.size(); rowId++) {
            addPriceCalculatorRowContent(sheet.createRow(rowId + 1), dtos.get(rowId), boldCellStyle, numberCellStyle);
        }

        return workbook;
    }

    private void addPriceCalculatorRowContent(Row row, DrinkPriceCalculatorDto dto, CellStyle boldCellStyle, CellStyle numberCellStyle) {
        var cellId = 0;
        writeContentToCell(row, cellId++, dto.getId());
        writeContentToCell(row, cellId++, dto.getBoughtDrinkId());
        writeContentToCell(row, cellId++, dto.getProducerName());
        writeContentToCell(row, cellId++, dto.getProducerOriginName());
        writeContentToCell(row, cellId++, dto.getName());
        writeContentToCell(row, cellId++, dto.getColorName());
        writeContentToCell(row, cellId++, dto.getStyleName());
        var abvCell = writeContentToCell(row, cellId++, dto.getAbv());
        var buyingPriceCell = writeContentToCell(row, cellId++, dto.getBuyingPrice());
        writeContentToCell(row, cellId++, dto.getServiceMethod());
        var volumeCell = writeContentToCell(row, cellId++, dto.getVolumeInCl());
        var sellingPriceCell = writeContentToCell(row, cellId++, dto.getSellingPrice(), boldCellStyle);
        var projectedPriceCell = writeProjectedPriceFormulaToCell(row, cellId++, dto, buyingPriceCell, volumeCell, numberCellStyle);
        writePricePerClAlcFormulaToCell(row, cellId++, sellingPriceCell, volumeCell, abvCell, numberCellStyle);
        writeProjectedMarginFormulaToCell(row, cellId++, sellingPriceCell, projectedPriceCell, numberCellStyle);
        writeTotalMarginFormulaToCell(row, cellId++, dto, buyingPriceCell, volumeCell, sellingPriceCell, numberCellStyle);
    }

    private void writeTotalMarginFormulaToCell(Row row, int cellId, DrinkPriceCalculatorDto dto, Cell buyingPriceCell, Cell volumeCell, Cell sellingPriceCell, CellStyle numberCellStyle) {
        var cell = row.createCell(cellId);
        cell.setCellStyle(numberCellStyle);

        var formula = ServiceMethod.TAP.name().equalsIgnoreCase(dto.getServiceMethod())
                ? String.format("%s-(%s*%s/100)", getCellAddress(sellingPriceCell), getCellAddress(buyingPriceCell), getCellAddress(volumeCell))
                : String.format("%s-%s", getCellAddress(sellingPriceCell), getCellAddress(buyingPriceCell));

        cell.setCellFormula(formula);
    }

    private void writeProjectedMarginFormulaToCell(Row row, int cellId, Cell sellingPriceCell, Cell projectedPriceCell, CellStyle numberCellStyle) {
        var cell = row.createCell(cellId);
        cell.setCellStyle(numberCellStyle);

        var formula = String.format("%s-%s", getCellAddress(sellingPriceCell), getCellAddress(projectedPriceCell));

        cell.setCellFormula(formula);
    }

    private void writePricePerClAlcFormulaToCell(Row row, int cellId, Cell sellingPriceCell, Cell volumeCell, Cell abvCell, CellStyle numberCellStyle) {
        var cell = row.createCell(cellId);
        cell.setCellStyle(numberCellStyle);

        var formula = String.format("%s/(%s*%s/100)",
                getCellAddress(sellingPriceCell), getCellAddress(volumeCell), getCellAddress(abvCell));

        cell.setCellFormula(formula);
    }

    private Cell writeProjectedPriceFormulaToCell(Row row, int cellId, DrinkPriceCalculatorDto dto, Cell buyingPriceCell, Cell volumeCell, CellStyle numberCellStyle) {
        var cell = row.createCell(cellId);
        cell.setCellStyle(numberCellStyle);

        var formula = ServiceMethod.TAP.name().equalsIgnoreCase(dto.getServiceMethod())
                ? String.format("%s*%s*%s/100", getCellAddress(buyingPriceCell), TAP_COEFFICIENT, getCellAddress(volumeCell))
                : String.format("%s*%s", getCellAddress(buyingPriceCell), BOTTLE_COEFFICIENT);

        cell.setCellFormula(formula);

        return cell;
    }

    private String getCellAddress(Cell buyingPriceCell) {
        return String.format(
                "%s%s",
                CellReference.convertNumToColString(buyingPriceCell.getColumnIndex()),
                buyingPriceCell.getAddress().getRow() + 1);
    }

    private Cell writeContentToCell(Row row, int cellId, String content) {
        var cell = row.createCell(cellId);
        Optional.ofNullable(content)
                .ifPresent(cell::setCellValue);
        return cell;
    }

    private Cell writeContentToCell(Row row, int cellId, Double content) {
        return writeContentToCell(row, cellId, content, null);
    }

    private Cell writeContentToCell(Row row, int cellId, Double content, CellStyle cellStyle) {
        var cell = row.createCell(cellId);
        Optional.ofNullable(cellStyle).ifPresent(style -> cell.setCellStyle(cellStyle));

        Optional.ofNullable(content)
                .ifPresent(cell::setCellValue);
        return cell;
    }

    private Cell writeContentToCell(Row row, int cellId, Long content) {
        var cell = row.createCell(cellId);
        Optional.ofNullable(content)
                .ifPresent(cell::setCellValue);
        return cell;
    }

    private CellStyle buildBoldCellStyle(Workbook workbook) {
        var boldStyle = workbook.createCellStyle();
        var boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        return boldStyle;
    }

    private CellStyle buildRoundedNumberCellStyle(Workbook workbook) {
        var style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        return style;
    }

    private void addHeader(Sheet sheet, CellStyle boldStyle, List<String> headers) {
        var row = sheet.createRow(0);

        for (var cellId = 0; cellId < headers.size(); cellId++) {
            var cell = row.createCell(cellId);
            cell.setCellValue(headers.get(cellId));
            cell.setCellStyle(boldStyle);
        }
    }
}
