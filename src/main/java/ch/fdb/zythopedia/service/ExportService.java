package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.*;
import ch.fdb.zythopedia.dto.mapper.*;
import ch.fdb.zythopedia.entity.BoughtDrink;
import ch.fdb.zythopedia.enums.ServiceMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
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
    private BoughtDrinkService boughtDrinkService;
    private DrinkPriceCalculatorDtoMapper drinkPriceCalculatorDtoMapper;
    private DrinkMapper drinkMapper;
    private SimpleDrinkMapper simpleDrinkMapper;

    public ExportService(BoughtDrinkService boughtDrinkService, DrinkPriceCalculatorDtoMapper drinkPriceCalculatorDtoMapper, DrinkMapper drinkMapper, SimpleDrinkMapper simpleDrinkMapper) {
        this.boughtDrinkService = boughtDrinkService;
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
        var workbook = buildDataExporterWorkbook(currentEditionDrinks);
        return saveWorkbookInTempFolder(workbook, "drinkData");
    }

    private File saveWorkbookInTempFolder(Workbook workbook, String fileBaseName) {
        var fileName = String.format("tmp/%s_%s.xlsx", UUID.randomUUID(), fileBaseName);
        try  (OutputStream fileOut = new FileOutputStream(fileName)) {
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
        buildStylesSheet(workbook, getStyles(drinks), boldCellStyle);
        buildColorsSheet(workbook, getColors(drinks), boldCellStyle);
        buildProducersSheet(workbook, getProducers(drinks), boldCellStyle);
        buildOriginsSheet(workbook, getOrigins(drinks), boldCellStyle);

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
            var row = sheet.createRow(rowId+1);
            var drink = simpleDrinks.get(rowId);

            writeContentToCell(row, cellId++, drink.getId());
            writeContentToCell(row, cellId++, drink.getName());
            writeContentToCell(row, cellId++, drink.getProducerId());
            writeContentToCell(row, cellId++, drink.getProducerName());
            writeContentToCell(row, cellId++, drink.getDescription());
            writeContentToCell(row, cellId++, drink.getAbv());
            writeContentToCell(row, cellId++, drink.getColorId());
            writeContentToCell(row, cellId++, drink.getColorName());
            writeContentToCell(row, cellId++, drink.getStyleId());
            writeContentToCell(row, cellId++, drink.getStyleName());
        }
        return sheet;
    }

    private Sheet buildOriginsSheet(Workbook workbook, List<OriginDto> origins, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("origins");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "shortName", "flag", "toDelete", "replaceBy"));

        for (var rowId = 0; rowId < origins.size(); rowId++) {
            var cellId = 0;
            var row = sheet.createRow(rowId+1);
            var origin = origins.get(rowId);

            writeContentToCell(row, cellId++, origin.getId());
            writeContentToCell(row, cellId++, origin.getName());
            writeContentToCell(row, cellId++, origin.getShortName());
            writeContentToCell(row, cellId++, origin.getFlag());
        }
        return sheet;
    }

    private Sheet buildProducersSheet(Workbook workbook, List<ProducerDto> producers, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("producers");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "originId", "originName", "toDelete", "replaceBy"));

        for (var rowId = 0; rowId < producers.size(); rowId++) {
            var cellId = 0;
            var row = sheet.createRow(rowId+1);
            var producer = producers.get(rowId);

            var optionalOrigin = Optional.ofNullable(producer.getOrigin());

            writeContentToCell(row, cellId++, producer.getId());
            writeContentToCell(row, cellId++, producer.getName());
            writeContentToCell(row, cellId++, optionalOrigin
                    .map(OriginDto::getId)
                    .map(String::valueOf)
                    .orElse(""));
            writeContentToCell(row, cellId++, optionalOrigin
                    .map(OriginDto::getName)
                    .orElse(""));
        }
        return sheet;
    }

    private Sheet buildStylesSheet(Workbook workbook, List<StyleDto> styles, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("styles");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "description", "parentId", "parentName", "toDelete", "replaceBy"));

        for (var rowId = 0; rowId < styles.size(); rowId++) {
            var cellId = 0;
            var row = sheet.createRow(rowId+1);
            var style = styles.get(rowId);

            writeContentToCell(row, cellId++, style.getId());
            writeContentToCell(row, cellId++, style.getName());
            writeContentToCell(row, cellId++, style.getDescription());
            writeContentToCell(row, cellId++, style.getParentId());
            writeContentToCell(row, cellId++, style.getParentName());
        }
        return sheet;
    }

    private Sheet buildColorsSheet(Workbook workbook, List<ColorDto> colors, CellStyle boldCellStyle) {
        var sheet = workbook.createSheet("colors");

        addHeader(sheet, boldCellStyle, List.of("id", "name", "description", "toDelete", "replaceBy"));

        for (var rowId = 0; rowId < colors.size(); rowId++) {
            var cellId = 0;
            var row = sheet.createRow(rowId+1);
            var color = colors.get(rowId);

            writeContentToCell(row, cellId++, color.getId());
            writeContentToCell(row, cellId++, color.getName());
            writeContentToCell(row, cellId++, color.getDescription());
        }
        return sheet;
    }

    private List<ColorDto> getColors(Collection<DrinkDto> drinks) {
        var list = drinks.stream()
                .map(DrinkDto::getColor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return list.stream()
                .sorted(Comparator.comparing(ColorDto::getName))
                .collect(Collectors.toList());
    }

    private List<StyleDto> getStyles(Collection<DrinkDto> drinks) {
        var list = drinks.stream()
                .map(DrinkDto::getStyle)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return list.stream()
            .sorted(Comparator.comparing(StyleDto::getName))
            .collect(Collectors.toList());
    }

    private List<ProducerDto> getProducers(Collection<DrinkDto> drinks) {
        var list = drinks.stream()
                .map(DrinkDto::getProducer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return list.stream()
                .sorted(Comparator.comparing(ProducerDto::getName))
                .collect(Collectors.toList());
    }

    private List<OriginDto> getOrigins(Collection<DrinkDto> drinks) {
        var list = drinks.stream()
                .map(DrinkDto::getProducer)
                .filter(Objects::nonNull)
                .map(ProducerDto::getOrigin)
                .collect(Collectors.toSet());
        return list.stream()
                .sorted(Comparator.comparing(OriginDto::getName))
                .collect(Collectors.toList());
    }

    private Workbook buildPriceCalculatorWorkbook(List<DrinkPriceCalculatorDto> dtos) {
        var workbook = (Workbook) new XSSFWorkbook();
        var sheet = workbook.createSheet();

        var boldCellStyle = buildBoldCellStyle(workbook);
        var numberCellStyle = buildRoundedNumberCellStyle(workbook);

        addHeader(sheet, boldCellStyle, PRICE_CALCULATOR_HEADERS);

        for (var rowId = 0; rowId < dtos.size(); rowId++) {
            addPriceCalculatorRowContent(sheet.createRow(rowId+1), dtos.get(rowId), boldCellStyle, numberCellStyle);
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
                buyingPriceCell.getAddress().getRow()+1);
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
