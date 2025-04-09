package fili5rovic.codegalaxy.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectHelper {

    public static void main(String[] args) {
        setWorkspace("D:\\MY_WORKSPACE");
        ProjectHelper.createProject("Test");
    }

    public static void setWorkspace(String workspacePath) {
        System.setProperty("workspace.path", workspacePath);
    }

    public static void createProject(String projectName) {
        try {
            // Combine base path and project name
            Path projectDir = Paths.get(System.getProperty("workspace.path")).resolve(projectName).toAbsolutePath();
            // Create project directory and subdirectories
            Files.createDirectories(projectDir.resolve("src"));
            Files.createDirectories(projectDir.resolve("lib"));
            Files.createDirectories(projectDir.resolve("bin"));

            // Create .classpath file
            Path classpathFile = projectDir.resolve(".classpath");
            String classPathContent = """
                    <?xml version="1.0" encoding="UTF-8"?>
                        <classpath>
                            <classpathentry kind="src" path="src"/>
                            <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
                            <classpathentry kind="output" path="bin"/>
                        </classpath>
                    
                    """;
            Files.writeString(classpathFile, classPathContent);

            // Create .project file
            Path projectFile = projectDir.resolve(".project");
            String projectFileContent = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <projectDescription>
                        <name>%s</name>
                        <comment></comment>
                        <projects></projects>
                        <buildSpec>
                            <buildCommand>
                                <name>org.eclipse.jdt.core.javabuilder</name>
                                <arguments></arguments>
                            </buildCommand>
                        </buildSpec>
                        <natures>
                            <nature>org.eclipse.jdt.core.javanature</nature>
                        </natures>
                    </projectDescription>
                    """.formatted(projectName);
            // remove .formatted later
            Files.writeString(projectFile, projectFileContent);

            // create Main.java
            Path mainFile = projectDir.resolve("src").resolve("Main.java");
            String mainFileContent = """
                    public class Main {
                        public static void main(String[] args) {
                            System.out.println("Hello, World!");
                        }
                    }
                    """;
            Files.writeString(mainFile, mainFileContent);

            System.out.println("Project created at: " + projectDir);

        } catch (IOException e) {
            System.err.println("Error creating project structure: " + e.getMessage());
        }
    }
}
