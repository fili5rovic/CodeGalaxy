package fili5rovic.codegalaxy.fileFinder;

import java.nio.file.Path;

public class FileItem {
    private final String fileName;
    private final Path filePath;

    public FileItem(Path path) {
        this.fileName = path.getFileName().toString();
        this.filePath = path;
    }

    public Path getFilePath() { return filePath; }
    @Override
    public String toString() { return fileName; }
}

