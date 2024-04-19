package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(path = "/api/import", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/order/amstein")
    @Secured("ROLE_ADMIN")
    public String importAmsteinOrder(@RequestParam("file") MultipartFile file) {
        var filename = file.getOriginalFilename();
        log.info("File uploaded : " + filename);

        importService.importAmsteinOrder(file);

        return filename;
    }

    @PostMapping(value = "/catalog/amstein")
    @Secured("ROLE_ADMIN")
    public String importAmsteinCatalog(@RequestParam("file") MultipartFile file) {
        var filename = file.getOriginalFilename();
        log.info("File uploaded : " + filename);

        importService.importAmsteinCatalogData(file);

        return filename;
    }

    @PostMapping(value = "/calculator")
    @Secured("ROLE_ADMIN")
    public String importPricesFromCalculator(@RequestParam("file") MultipartFile file) {
        var filename = file.getOriginalFilename();
        log.info("File uploaded : " + filename);

        importService.importPrices(file);

        return filename;
    }

    @PostMapping(value = "/data")
    @Secured("ROLE_ADMIN")
    public String importDrinkData(@RequestParam("file") MultipartFile file) {
        var filename = file.getOriginalFilename();
        log.info("File uploaded : " + filename);

        importService.importDrinksData(file);

        return filename;
    }
}
