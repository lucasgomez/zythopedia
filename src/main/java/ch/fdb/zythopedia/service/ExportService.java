package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.DrinkPriceCalculatorDto;
import ch.fdb.zythopedia.dto.mapper.DrinkPriceCalculatorDtoMapper;
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
    private BoughtDrinkService boughtDrinkService;
    private DrinkPriceCalculatorDtoMapper drinkPriceCalculatorDtoMapper;

    public ExportService(BoughtDrinkService boughtDrinkService, DrinkPriceCalculatorDtoMapper drinkPriceCalculatorDtoMapper) {
        this.boughtDrinkService = boughtDrinkService;
        this.drinkPriceCalculatorDtoMapper = drinkPriceCalculatorDtoMapper;
    }

    @Transactional
    public File getCalculatorForCurrentEdition() {
        var calculatorLines = boughtDrinkService.getCurrentEditionBoughtDrinks().stream()
                .map(BoughtDrink::getServices)
                .flatMap(List::stream)
                .map(drinkPriceCalculatorDtoMapper::toDto)
                .sorted(Comparator.comparing(DrinkPriceCalculatorDto::getBoughtDrinkId, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        var workbook = buildWorkbook(calculatorLines);
        return saveWorkbookInTempFolder(workbook);
    }

    private File saveWorkbookInTempFolder(Workbook workbook) {
        var fileName = String.format("tmp/%s_price_calculator.xlsx", UUID.randomUUID());
        try  (OutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            return new File(fileName);
        } catch (IOException ioException) {
            log.error("Could not save file to export due to some mysterious reason", ioException);
            throw new RuntimeException("Could not save file to export due to some mysterious reason", ioException);
        }
    }

    private Workbook buildWorkbook(List<DrinkPriceCalculatorDto> dtos) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet();

        var boldCellStyle = buildBoldCellStyle(workbook);
        var numberCellStyle = buildRoundedNumberCellStyle(workbook);

        addHeader(sheet, boldCellStyle);

        for (var rowId = 0; rowId < dtos.size(); rowId++) {
            addRowContent(sheet.createRow(rowId+1), dtos.get(rowId), boldCellStyle, numberCellStyle);
        }

        return workbook;
    }

    private void addRowContent(Row row, DrinkPriceCalculatorDto dto, CellStyle boldCellStyle, CellStyle numberCellStyle) {
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

    private void addHeader(Sheet sheet, CellStyle boldStyle) {
        var row = sheet.createRow(0);
        var headers = List.of("id", "boughtDrinkId", "producerName", "producerOriginName", "name", "colorName",
                "styleName", "abv", "buyingPrice", "serviceMethod", "volumeInCl", "sellingPrice",
                "projectedSellingPrice", "price per alc. cL", "margin");

        for (var cellId = 0; cellId < headers.size(); cellId++) {
            var cell = row.createCell(cellId);
            cell.setCellValue(headers.get(cellId));
            cell.setCellStyle(boldStyle);
        }
    }
}
