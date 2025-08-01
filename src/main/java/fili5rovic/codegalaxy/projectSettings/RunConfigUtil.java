package fili5rovic.codegalaxy.projectSettings;

import fili5rovic.codegalaxy.dashboardHelper.EditConfigurationsManager;
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

    public static void writeRunConfigurations() throws IOException {
        RunConfiguration[] configs = EditConfigurationsManager.getConfigurations().toArray(new RunConfiguration[0]);
        Path file = getRunConfigFilePath();
        JsonUtil.writeJson(file, configs);
    }




}
