package fili5rovic.codegalaxy.git;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class GitBasicUtil {

    private Git git;

    public void init(String repositoryPath) {
        File repoDir = new File(repositoryPath);
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid path: " + repositoryPath);
        }

        try {
            this.git = Git.init().setDirectory(repoDir).call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to initialize Git repository", e);
        }
    }

    public void open(String repositoryPath) {
        File repoDir = new File(repositoryPath);
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid path: " + repositoryPath);
        }

        File gitDir = new File(repoDir, ".git");
        if (!gitDir.exists() || !gitDir.isDirectory()) {
            throw new IllegalStateException("Not a Git repository: " + repositoryPath);
        }

        try {
            this.git = Git.open(repoDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Git repository", e);
        }
    }

    public void add(String filepattern) {
        checkGitOpen();
        try {
            AddCommand addCommand = git.add().addFilepattern(filepattern);
            addCommand.call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to add file(s)", e);
        }
    }

    public void commit(String message) {
        checkGitOpen();
        try {
            CommitCommand commitCommand = git.commit().setMessage(message);
            commitCommand.call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to commit", e);
        }
    }

    public void close() {
        if (git != null) {
            git.close();
            git = null;
        }
    }

    private void checkGitOpen() {
        if (git == null) {
            throw new IllegalStateException("Git repository is not open or initialized");
        }
    }

    public static void main(String[] args) {
        GitBasicUtil gitUtil = new GitBasicUtil();

        String path = "C:\\Users\\fili5\\OneDrive\\Desktop\\gaySex";

        File gitFolder = new File(path, ".git");
        if (gitFolder.exists()) {
            gitUtil.open(path);
        } else {
            gitUtil.init(path);
        }

        gitUtil.add(".");
        gitUtil.commit("Initial commit");
        gitUtil.close();
    }
}
