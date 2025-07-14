package fili5rovic.codegalaxy.projectSettings;

import fili5rovic.codegalaxy.projectSettings.dataclass.VcsSettings;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Path;

public class VCSUtil {

    private static Path getSettingsDir() {
        String lastProjectPath = IDESettings.getInstance().get("lastProjectPath");
        if (lastProjectPath == null || lastProjectPath.isEmpty()) {
            throw new IllegalStateException("No last project path set");
        }
        return Path.of(lastProjectPath).resolve(".codegalaxy");
    }

    private static Path getVcsFilePath() {
        return getSettingsDir().resolve("vcs.json");
    }

    public static VcsSettings readVcsSettings() throws IOException {
        Path vcsFile = getVcsFilePath();
        return JsonUtil.readJson(vcsFile, VcsSettings.class);
    }

    private static void writeVcsSettings(VcsSettings settings) throws IOException {
        Path vcsFile = getVcsFilePath();
        JsonUtil.writeJson(vcsFile, settings);
    }

    public static void setVCSRepoPath(String repositoryPath) {
        try {
            VcsSettings settings = readVcsSettings();
            settings.setRepositoryPath(repositoryPath);
            writeVcsSettings(settings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isVCSInit() {
        try {
            VcsSettings settings = readVcsSettings();
            return settings.getRepositoryPath() != null && !settings.getRepositoryPath().isEmpty();
        } catch (IOException e) {
            System.err.println("Failed to read VCS settings: " + e.getMessage());
            return false;
        }
    }

}
