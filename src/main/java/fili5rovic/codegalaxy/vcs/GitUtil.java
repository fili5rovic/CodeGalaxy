package fili5rovic.codegalaxy.vcs;

import fili5rovic.codegalaxy.vcs.treeView.GitHierarchy;
import fili5rovic.codegalaxy.projectSettings.ProjectSettingsUtil;

public class GitUtil {
    private final GitBasicUtil gitBasicUtil;

    private static GitUtil instance;

    private GitHierarchy gitHierarchy;

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
        GitFileHelper.makeGitIgnore(repositoryPath);
        ProjectSettingsUtil.setVCSRepoPath(repositoryPath);
    }

    public void open(String repositoryPath) {
        if(!ProjectSettingsUtil.isVCSInit())
            gitBasicUtil.init(repositoryPath);
        gitBasicUtil.open(repositoryPath);
        ProjectSettingsUtil.setVCSRepoPath(repositoryPath);
    }

    public void updateHierarchy() {
        if(gitHierarchy == null)
            return;
        gitHierarchy.update(gitBasicUtil.status());
    }

    public void commit(String message) {
        gitBasicUtil.commit(message);
    }

    public void restore(String filePattern) {
        gitBasicUtil.restore(filePattern);
    }

    public void add(String filePattern) {
        gitBasicUtil.add(filePattern);
    }

    public int getFileCountInLastCommit() throws Exception {
        return GitAdvancedUtil.getChangedFileCount(gitBasicUtil.getRepository());
    }

    public void setHierarchy(GitHierarchy hierarchy) {
        this.gitHierarchy = hierarchy;
    }
}
