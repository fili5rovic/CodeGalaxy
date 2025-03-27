package fili5rovic.codegalaxy.code.manager.file;

import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.code.manager.Manager;
import fili5rovic.codegalaxy.util.FileHelper;

import java.nio.file.Path;

public class FileManager extends Manager {

    private final Path path;

    public FileManager(CodeGalaxy cg, Path path) {
        super(cg);
        this.path = path;
    }

    @Override
    public void init() {
        codeGalaxy.insertText(0, FileHelper.readFromFile(path.toString()));
    }

    public void save() {
        FileHelper.writeToFile(path.toString(), codeGalaxy.getText());
    }



}
