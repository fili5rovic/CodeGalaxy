package fili5rovic.codegalaxy.projectSettings.dataclass;

public class VcsSettings {
    private String vcsType;
    private String repositoryPath;

    public VcsSettings() {
        this.vcsType = "git";
        this.repositoryPath = "";
    }

    public String getVcsType() { return vcsType; }
    public void setVcsType(String vcsType) { this.vcsType = vcsType; }

    public String getRepositoryPath() { return repositoryPath; }
    public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
}
