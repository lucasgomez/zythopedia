package ch.fdb.zythopedia.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public final class FileHelper {

    private FileHelper() {
    }

    public static File toFile(MultipartFile multipartFile) {
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
