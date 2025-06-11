package fili5rovic.codegalaxy.git;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

class GitBasicUtil {

    private Git git;

    public void open(String repositoryPath) {
        File repoDir = new File(repositoryPath);

        if (!repoDir.exists())
            throw new IllegalArgumentException("The specified path does not exist: " + repositoryPath);

        if (!repoDir.isDirectory())
            throw new IllegalArgumentException("The specified path is not a directory: " + repositoryPath);

        File gitDir = new File(repoDir, ".git");
        boolean isGitRepo = gitDir.exists() && gitDir.isDirectory();

        try {
            if (isGitRepo)
                this.git = Git.open(repoDir);
            else
                this.git = Git.init().setDirectory(repoDir).call();

        } catch (GitAPIException | IOException e) {
            throw new RuntimeException("Failed to open or init repo", e);
        }
    }

    public void close() {
        if (git != null) {
            git.close();
            git = null;
        }
    }

    public static void main(String[] args) {
        GitBasicUtil gitUtil = new GitBasicUtil();
        gitUtil.open("C:\\Users\\fili5\\OneDrive\\Desktop\\gaySex");
        gitUtil.close();
    }


}
