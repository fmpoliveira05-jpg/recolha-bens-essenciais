package recolha.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lê os documentos JSON de uma pasta local (por omissão, {@code data/}).
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class FileDataSource implements DataSource {

    private final Path directory;

    /**
     * @param directory pasta com os ficheiros {@code aidBoxes.json}, {@code containers.json}, ...
     */
    public FileDataSource(Path directory) {
        this.directory = directory;
    }

    @Override
    public String load(Dataset dataset) throws IOException {
        Path file = this.directory.resolve(dataset.getFileName());
        if (!Files.isRegularFile(file)) {
            throw new IOException("Ficheiro não encontrado: " + file.toAbsolutePath());
        }
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    @Override
    public String describe() {
        return "pasta " + this.directory.toAbsolutePath().normalize();
    }
}
