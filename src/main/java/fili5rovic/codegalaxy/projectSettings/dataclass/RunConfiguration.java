package fili5rovic.codegalaxy.projectSettings.dataclass;

public class RunConfiguration {
    private String configName;
    private String programArgs;
    private String fullName;

    // default constructor for deserialization
    public RunConfiguration() {}

    public RunConfiguration(String configName, String programArgs, String fullName) {
        this.configName = configName;
        this.programArgs = programArgs;
        this.fullName = fullName;
    }

    public String getConfigName() {
        return configName;
    }

    public String getProgramArgs() {
        return programArgs;
    }

    public String getFullName() {
        return fullName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public void setProgramArgs(String programArgs) {
        this.programArgs = programArgs;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return configName;
    }
}
