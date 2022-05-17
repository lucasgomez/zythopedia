package ch.fdb.zythopedia.service;

import ch.fdb.zythopedia.dto.creation.CreateBoughtDrinkDto;
import ch.fdb.zythopedia.enums.ServiceMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@Service
public class AmsteinImporterService {

    private static final int CODE_COLUMN_NUM = 1;
    private static final int NAME_COLUMN_NUM = 2;
    private static final int BUYING_PRICE_COLUMN_NUM = 5;
    private static final int CONTENT_TYPE_COLUMN_NUM = 4;

    public Collection<CreateBoughtDrinkDto> importFromFile(MultipartFile multipartFile) {
        var file = toFile(multipartFile);

        try (var inputStream = new FileInputStream(file)) {
            var workbook = WorkbookFactory.create(inputStream);
            var sheet = workbook.getSheetAt(0);

            var rowNum = 1;
            var boughtDrinksToImport = new ArrayList<CreateBoughtDrinkDto>();
            while (rowHasContent(sheet, rowNum)) {
                var boughtDrinkToImport = getBoughtDrinkFromRow(sheet.getRow(rowNum));
                if (null != boughtDrinkToImport.getServiceMethod()) {
                    boughtDrinksToImport.add(boughtDrinkToImport);
                }
                rowNum++;
            }
            return boughtDrinksToImport;
        } catch (FileNotFoundException fileNotFoundException) {
            log.error("File not found", fileNotFoundException);
            throw new IllegalArgumentException("File not found", fileNotFoundException);
        } catch (IOException e) {
            log.error("Could not read file", e);
            throw new IllegalArgumentException("Could not read file", e);
        }
    }

    private CreateBoughtDrinkDto getBoughtDrinkFromRow(org.apache.poi.ss.usermodel.Row row) {
        return CreateBoughtDrinkDto.builder()
                .code(row.getCell(CODE_COLUMN_NUM).getStringCellValue())
                .name(row.getCell(NAME_COLUMN_NUM).getStringCellValue())
                .buyingPrice(row.getCell(BUYING_PRICE_COLUMN_NUM).getNumericCellValue())
                .serviceMethod(getServiceMethodFromAmsteinContentType(row.getCell(CONTENT_TYPE_COLUMN_NUM).getStringCellValue()))
                .build();
    }

    private ServiceMethod getServiceMethodFromAmsteinContentType(String contentType) {
        switch (contentType) {
            case "FUT": return ServiceMethod.TAP;
            case "CAR": return ServiceMethod.BOTTLE;
            default: return null;
        }
    }

    private boolean rowHasContent(org.apache.poi.ss.usermodel.Sheet sheet, int rowNum) {
        return null != sheet.getRow(rowNum)
                && null != sheet.getRow(rowNum).getCell(CODE_COLUMN_NUM)
                && !sheet.getRow(rowNum).getCell(CODE_COLUMN_NUM).getStringCellValue().isBlank();
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
}
