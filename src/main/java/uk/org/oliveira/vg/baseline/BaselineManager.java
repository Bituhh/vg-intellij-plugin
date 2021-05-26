package uk.org.oliveira.vg.baseline;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

public class BaselineManager {
    public static Path moveFileToCurrentFolder(String sourcePath) throws IOException {
        Path source = Path.of(sourcePath);
        Path target = Path.of(source.toString().replace("postgres/schema", "/postgres/release/current/schema"));
        if (source.equals(target)) {
            target = Path.of(source.toString().replace("\\postgres\\schema", "/postgres\\release\\current\\schema"));
        }
        Files.createDirectories(target.getParent());
        Files.copy(source, target);

        return target;
    }
}
