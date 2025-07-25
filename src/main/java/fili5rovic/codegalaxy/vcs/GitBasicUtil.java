package fili5rovic.codegalaxy.vcs;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;

class GitBasicUtil {

    private Git git;

    private boolean opened = false;

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

    public Repository getRepository() {
        if (git == null) {
            throw new IllegalStateException("Git repository is not open or initialized");
        }
        return git.getRepository();
    }

    public void open(String repositoryPath) {
        if (opened)
            return;

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
        this.opened = true;
    }

    public void add(String filePattern) {
        if (git == null)
            throw new IllegalStateException("Git repository is not open or initialized");
        try {
            AddCommand addCommand = git.add().addFilepattern(filePattern);
            addCommand.call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to add file(s)", e);
        }
    }

    public void commit(String message) {
        if (git == null)
            throw new IllegalStateException("Git repository is not open or initialized");
        try {
            CommitCommand commitCommand = git.commit().setMessage(message);
            commitCommand.call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to commit", e);
        }
    }

    public void restore(String filePattern) {
        if (git == null)
            throw new IllegalStateException("Git repository is not open or initialized");
        try {
            git.reset().addPath(filePattern).call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to restore file(s)", e);
        }
    }

    public Status status() {
        if (git == null)
            throw new IllegalStateException("Git repository is not open or initialized");

        try {
            return git.status().call();


        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to get git status", e);
        }
    }

    public void close() {
        if (git != null) {
            git.close();
            git = null;
        }
        opened = false;
    }
}
