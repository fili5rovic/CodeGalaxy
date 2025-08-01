package fili5rovic.codegalaxy.projectSettings.dataclass;

public class RunConfiguration {
    private static int ID = 1;
    private int id;
    private String configName;
    private String fullName;
    private String[] programArgs;
    private String[] vmOptions;

    public static final RunConfiguration EDIT = new RunConfiguration("Edit Configurations", "", new String[0], new String[0]);

    // default constructor for deserialization
    public RunConfiguration() {}

    public RunConfiguration(String configName, String fullName, String[] programArgs, String[] vmOptions) {
        this.configName = configName;
        this.programArgs = programArgs;
        this.fullName = fullName;
        this.vmOptions = vmOptions;
        this.id = ID++;
    }

    public String getConfigName() {
        return configName;
    }

    public String[] getProgramArgs() {
        return programArgs;
    }

    public String getFullName() {
        return fullName;
    }

    public String[] getVmOptions() {
        return vmOptions;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public void setProgramArgs(String[] programArgs) {
        this.programArgs = programArgs;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setVmOptions(String[] vmOptions) {
        this.vmOptions = vmOptions;
    }

    @Override
    public String toString() {
        return configName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RunConfiguration)) return false;
        RunConfiguration that = (RunConfiguration) obj;
        return this.id == that.id;
    }


}
