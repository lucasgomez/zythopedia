package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.service.ExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export/calculator")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ByteArrayResource> getCalculatorForCurrentEdition() {
        return buildResponseEntity(exportService.getCalculatorForCurrentEdition());
    }

    @GetMapping("/edition/{editionName}/export/data")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ByteArrayResource> getDrinksData(@PathVariable String editionName) {
        return buildResponseEntity(exportService.getDrinksData(editionName));
    }

    private ResponseEntity<ByteArrayResource> buildResponseEntity(File file) {
        var bytes = getBytes(file);
        return ResponseEntity.ok()
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename = \"" + file.getName() + "\"")
                .body(new ByteArrayResource(bytes));
    }

    private byte[] getBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException ioException) {
            log.error("Could not transform file into byte[]", ioException);
            throw new RuntimeException("Could not transform file into byte[]", ioException);
        }
    }
}
