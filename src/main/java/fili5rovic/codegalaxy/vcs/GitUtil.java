package fili5rovic.codegalaxy.vcs;

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
        gitBasicUtil.init(repositoryPath);
    }
}
