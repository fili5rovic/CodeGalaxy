package fili5rovic.codegalaxy.projectSetings;

import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.JsonUtil;
import fili5rovic.codegalaxy.projectSetings.dataclass.VcsSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectSettingsUtil {

    public static void ensureProjectSettingsInitialized(Path projectDir) {
        try {
            Path settingsDir = projectDir.resolve(".codegalaxy");

            if (Files.notExists(settingsDir)) {
                Files.createDirectories(settingsDir);
            }

            Path vcsFile = settingsDir.resolve("vcs.json");
            if (Files.notExists(vcsFile)) {
                Files.createFile(vcsFile);
                JsonUtil.writeJson(vcsFile, new VcsSettings());
            }

        } catch (IOException e) {
            System.err.println("Failed to create .codegalaxy project settings: " + e.getMessage());
        }
    }

    public static void setVCSRepoPath(String repositoryPath) {
        VCSUtil.setVCSRepoPath(repositoryPath);
    }

    public static boolean isVCSInit() {
        return VCSUtil.isVCSInit();
    }

}
