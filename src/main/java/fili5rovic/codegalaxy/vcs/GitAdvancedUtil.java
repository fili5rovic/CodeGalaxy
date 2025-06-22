package fili5rovic.codegalaxy.vcs;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class GitAdvancedUtil {

    public static int getChangedFileCount(Repository repository) throws Exception {
        ObjectId headId = repository.resolve("HEAD");

        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(headId);

            if (commit.getParentCount() == 0) {
                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(commit.getTree());
                    treeWalk.setRecursive(true);
                    int count = 0;
                    while (treeWalk.next()) count++;
                    return count;
                }
            }

            RevCommit parent = revWalk.parseCommit(commit.getParent(0).getId());

            try (ObjectReader reader = repository.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, parent.getTree());

                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, commit.getTree());

                try (DiffFormatter diffFormatter = new DiffFormatter(new ByteArrayOutputStream())) {
                    diffFormatter.setRepository(repository);
                    List<DiffEntry> diffs = diffFormatter.scan(oldTreeIter, newTreeIter);
                    return diffs.size();
                }
            }
        }
    }
}
