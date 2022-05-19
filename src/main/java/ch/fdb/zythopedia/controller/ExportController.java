package ch.fdb.zythopedia.controller;

import ch.fdb.zythopedia.service.ExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@RestController
@RequestMapping(path = "/api/export", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "*/*")
public class ExportController {

    private ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/calculator")
    public ResponseEntity<ByteArrayResource> getCalculatorForCurrentEdition() {
        var file = exportService.getCalculatorForCurrentEdition();
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
