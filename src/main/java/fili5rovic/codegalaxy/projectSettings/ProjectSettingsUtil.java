package fili5rovic.codegalaxy.projectSettings;

import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.JsonUtil;
import fili5rovic.codegalaxy.projectSettings.dataclass.VcsSettings;

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

            Path runConfigFile = settingsDir.resolve("runConfigurations.json");
            if (Files.notExists(runConfigFile)) {
                Files.createFile(runConfigFile);
            }

        } catch (IOException e) {
            System.err.println("Failed to create .codegalaxy project settings: " + e.getMessage());
        }
    }

    public static Path getSettingsDir() {
        String lastProjectPath = IDESettings.getRecentInstance().get("lastProjectPath");
        if (lastProjectPath == null || lastProjectPath.isEmpty()) {
            throw new IllegalStateException("No last project path set");
        }
        return Path.of(lastProjectPath).resolve(".codegalaxy");
    }

    public static void setVCSRepoPath(String repositoryPath) {
        VCSUtil.setVCSRepoPath(repositoryPath);
    }

    public static boolean isVCSInit() {
        if(IDESettings.getInstance().get("lastProjectPath") == null) {
            return false;
        }
        return VCSUtil.isVCSInit();
    }

}
