package fili5rovic.codegalaxy.vcs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class GitFileHelper {

    public static void makeGitIgnore(String repositoryPath) {
        String gitIgnoreContent = """
                # Ignore the .idea directory
                .idea/
                .codegalaxy/
                
                # Ignore build directories
                /build/
                /out/
                
                # Ignore compiled Java classes
                *.class
                
                # Ignore JAR files
                *.jar
                
                # Ignore log files
                *.log
                
                # Ignore temporary files
                *.tmp
                
                # Ignore user-specific files
                *.user
                """;

        try {
            Path gitIgnorePath = Paths.get(repositoryPath, ".gitignore");

            if (!Files.exists(gitIgnorePath)) {
                Files.writeString(gitIgnorePath, gitIgnoreContent);
                System.out.println(".gitignore file created successfully.");
            } else {
                System.out.println(".gitignore file already exists.");
            }
        } catch (Exception e) {
            System.err.println("Failed to create .gitignore file: " + e.getMessage());
        }

    }
}
