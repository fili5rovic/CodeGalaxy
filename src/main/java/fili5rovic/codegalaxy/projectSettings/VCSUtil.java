package fili5rovic.codegalaxy.projectSettings;

import fili5rovic.codegalaxy.projectSettings.dataclass.VcsSettings;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VCSUtil {

    private static Path getVcsFilePath() {
        return ProjectSettingsUtil.getSettingsDir().resolve("vcs.json");
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

    public static Path findGitRoot(Path start) {
        Path current = start.toAbsolutePath();

        while (current != null) {
            Path gitDir = current.resolve(".git");
            if (Files.isDirectory(gitDir)) {
                return current;
            }

            current = current.getParent();
        }

        return null;
    }


    public static boolean isVCSInit() {
        VcsSettings settings = null;
        try {
            settings = readVcsSettings();
        } catch (IOException e) {
            System.err.println("Failed to read vcs.json filepath.");
        }

        if (settings != null) {
            String repoPath = settings.getRepositoryPath();

            if (repoPath != null && !repoPath.isEmpty()) {

                Path repo = Paths.get(repoPath);
                if (Files.isDirectory(repo)) {
                    Path gitFolder = repo.resolve(".git");

                    if (Files.isDirectory(gitFolder)) {
                        return true;
                    }
                }
            }
        }
        // fallback: find .git in parent
        String projectPathStr = IDESettings.getRecentInstance().get("lastProjectPath");
        Path projectPath = Paths.get(projectPathStr);

        Path repoRoot;
        try {
            repoRoot = findGitRoot(projectPath);
        } catch (Exception e) {
            System.err.println("Exception in fallback findGitRoot: " + e.getMessage());
            return false;
        }

        if (repoRoot == null) {
            return false;
        }

        try {
            if (settings == null) {
                settings = new VcsSettings();
            }

            settings.setRepositoryPath(repoRoot.toString());
            writeVcsSettings(settings);

            return true;
        } catch (IOException e) {
            System.err.println("Failed to write fallback repoPath: " + e.getMessage());
            return false;
        }
    }


}
