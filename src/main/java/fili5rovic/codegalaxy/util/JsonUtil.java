package fili5rovic.codegalaxy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T readJson(Path path, Class<T> clazz) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }
        return mapper.readValue(path.toFile(), clazz);
    }

    public static void writeJson(Path path, Object obj) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), obj);
    }
}
