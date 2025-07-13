package fili5rovic.codegalaxy.dashboardHelper;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.controller.Controllers;
import fili5rovic.codegalaxy.hierarchy.ProjectHierarchy;
import fili5rovic.codegalaxy.hierarchy.ProjectItem;
import fili5rovic.codegalaxy.lsp.LSP;
import fili5rovic.codegalaxy.projectSetings.ProjectSettingsUtil;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.util.FileHelper;
import fili5rovic.codegalaxy.window.Window;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ProjectManager {


    private static ProjectHierarchy projectHierarchy;

    public static void openProject(Path path) {
        ProjectSettingsUtil.ensureProjectSettingsInitialized(path);

        projectHierarchy = new ProjectHierarchy(path);

        Controllers.dashboardController().getTreeViewPane().setCenter(projectHierarchy);
        IDESettings.getInstance().set("lastProjectPath", path.toString());
    }

    public static void reloadHierarchy() {
        projectHierarchy.reloadHierarchy();
    }

    public static void reloadHierarchy(ProjectItem item) {
        projectHierarchy.reloadHierarchy(item);
    }

    public static void errorOnPath(Path path, boolean isError) {
        if (projectHierarchy == null) {
            return;
        }
        projectHierarchy.errorOnPath(path, isError);
    }

    public static void setWorkspace(String workspacePath) {
        IDESettings.getInstance().set("workspace", workspacePath);
    }

    public static void createProject(String projectName) {
        try {
            String basePath = IDESettings.getInstance().get("workspace");
            if (basePath == null || basePath.isEmpty()) {
                System.err.println("Workspace path is not set. Please set the workspace path first.");
                return;
            }
            Path projectDir = Paths.get(basePath).resolve(projectName).toAbsolutePath();

            ProjectSettingsUtil.ensureProjectSettingsInitialized(projectDir);

            Files.createDirectories(projectDir.resolve("src"));
            Files.createDirectories(projectDir.resolve("lib"));
            Files.createDirectories(projectDir.resolve("bin"));

            // create .classpath file
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

            // create .project file
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

            Files.writeString(projectFile, projectFileContent);


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

            LSP.instance().sendFolderChange(projectDir.toString());

            openProject(projectDir);

        } catch (IOException e) {
            System.err.println("Error creating project structure: " + e.getMessage());
        }
    }

    public static void tryToOpenLastProject() {
        String lastProjectPath = IDESettings.getInstance().get("lastProjectPath");
        if (lastProjectPath == null)
            return;
        File lastProjectFile = new File(lastProjectPath);
        Path lastProjectPathFile = lastProjectFile.toPath();
        if (lastProjectFile.exists() && lastProjectFile.isDirectory()) {
            openProject(lastProjectPathFile);
        } else {
            System.out.println("Last project path is not valid.");
        }

        List<String> recentFiles = IDESettings.getInstance().getMultiple("recentFiles");
        for (String filePath : recentFiles) {
            Path path = Path.of(filePath);
            if (path.toFile().exists()) {
                Controllers.dashboardController().createTab(path);
            }
        }
    }

    public static CompletableFuture<Boolean> checkForValidWorkspace() {
        String workspacePath = IDESettings.getInstance().get("workspace");
        if (workspacePath != null && !workspacePath.isEmpty() && Files.exists(Paths.get(workspacePath))) {
            return CompletableFuture.completedFuture(true);
        }

        File folder = null;
        while(folder == null || !folder.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initOwner(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage());
            alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/fili5rovic/codegalaxy/css/main-dark.css")).toExternalForm());
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/fili5rovic/codegalaxy/png/app/codeGalaxy.png"))));
            alert.getDialogPane().setGraphic(null);


            alert.setTitle("Set valid workspace");
            alert.setHeaderText("Valid workspace not set");
            alert.setContentText("Please select a valid workspace directory to continue.");

            ButtonType chooseButton = new ButtonType("Choose", ButtonBar.ButtonData.OK_DONE);
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(chooseButton, closeButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() == closeButton) {
                return CompletableFuture.completedFuture(false);
            }

            folder = FileHelper.openFolderChooser(Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage(), null);
        }

        setWorkspace(folder.getAbsolutePath());
        System.out.println("Workspace set to: " + folder.getAbsolutePath());
        return CompletableFuture.completedFuture(true);
    }

}
