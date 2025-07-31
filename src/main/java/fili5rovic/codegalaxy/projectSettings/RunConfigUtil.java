package fili5rovic.codegalaxy.projectSettings;

import fili5rovic.codegalaxy.projectSettings.dataclass.RunConfiguration;
import fili5rovic.codegalaxy.util.JsonUtil;

import java.io.IOException;
import java.nio.file.Path;

public class RunConfigUtil {

    private static Path getRunConfigFilePath() {
        return ProjectSettingsUtil.getSettingsDir().resolve("runConfigurations.json");
    }

    public static RunConfiguration[] readRunConfigurations() throws IOException {
        Path file = getRunConfigFilePath();
        return JsonUtil.readJson(file, RunConfiguration[].class);
    }

    public static void writeRunConfigurations(RunConfiguration[] configs) throws IOException {
        Path file = getRunConfigFilePath();
        JsonUtil.writeJson(file, configs);
    }




}
