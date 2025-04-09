package fili5rovic.codegalaxy.project;

import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.hierarchy.ProjectHierarchy;
import fili5rovic.codegalaxy.preferences.UserPreferences;
import fili5rovic.codegalaxy.window.Window;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectManager {

    private static final DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);

    public static void openProject(Path path) {
        controller.getTreeViewPane().setCenter(new ProjectHierarchy(path.toString()));
    }

    public static void setWorkspace(String workspacePath) {
        UserPreferences.getInstance().set("workspace", workspacePath);
    }

    public static void createProject(String projectName) {
        try {
            String basePath = UserPreferences.getInstance().get("workspace");
            Path projectDir = Paths.get(basePath).resolve(projectName).toAbsolutePath();

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
                            int a = 3;
                            System.out.println("Hello, World!");
                        }
                    }
                    """;
            Files.writeString(mainFile, mainFileContent);

            System.out.println("Project created at: " + projectDir);

            // Open the project in the tree view
            openProject(projectDir);

        } catch (IOException e) {
            System.err.println("Error creating project structure: " + e.getMessage());
        }
    }



}
