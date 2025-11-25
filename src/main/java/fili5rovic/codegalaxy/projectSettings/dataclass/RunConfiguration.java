package fili5rovic.codegalaxy.projectSettings.dataclass;

import java.util.UUID;
import java.util.Objects;

public class RunConfiguration {
    private String id;
    private String configName;
    private String fullName;
    private String[] programArgs;
    private String[] vmOptions;

    public static final RunConfiguration EDIT = new RunConfiguration("Edit Configurations", "", new String[0], new String[0]);

    public RunConfiguration() {
        this.id = UUID.randomUUID().toString();
    }

    public RunConfiguration(String configName, String fullName, String[] programArgs, String[] vmOptions) {
        this.id = UUID.randomUUID().toString();
        this.configName = configName;
        this.programArgs = programArgs;
        this.fullName = fullName;
        this.vmOptions = vmOptions;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RunConfiguration)) return false;
        RunConfiguration that = (RunConfiguration) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
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

    public void setId(String id) {
        this.id = id;
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


}