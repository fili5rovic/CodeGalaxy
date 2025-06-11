package fili5rovic.codegalaxy.vcs;

import fili5rovic.codegalaxy.projectSetings.ProjectSettingsUtil;

public class GitUtil {
    private final GitBasicUtil gitBasicUtil;

    private static GitUtil instance;

    private GitUtil() {
        this.gitBasicUtil = new GitBasicUtil();
    }

    public static GitUtil instance() {
        if (instance == null)
            instance = new GitUtil();
        return instance;
    }

    public void init(String repositoryPath) {
        if(ProjectSettingsUtil.isVCSInit())
            return;
        gitBasicUtil.init(repositoryPath);
        ProjectSettingsUtil.setVCSRepoPath(repositoryPath);
    }
}
