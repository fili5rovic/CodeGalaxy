package fili5rovic.codegalaxy.util;

import fili5rovic.codegalaxy.Main;
import fili5rovic.codegalaxy.code.CodeGalaxy;
import fili5rovic.codegalaxy.controller.DashboardController;
import fili5rovic.codegalaxy.settings.IDESettings;
import fili5rovic.codegalaxy.window.Window;
import javafx.collections.ObservableList;
import javafx.scene.Scene;

import java.net.URL;
import java.util.Objects;

public class CSSUtil {

    public static boolean isDarkTheme() {
        return "dark".equals(IDESettings.getInstance().get("theme"));
    }

    public static void applyStylesheet(ObservableList<String> stylesheet, String name) {
        String theme = IDESettings.getInstance().get("theme");
        String otherTheme = theme.equals("light") ? "dark" : "light";

        String newPath = toExternalForm(name + "-" + theme + ".css");
        String oldPath = toExternalFormNullable(name + "-" + otherTheme + ".css");

        if (oldPath != null) stylesheet.remove(oldPath);
        if (!stylesheet.contains(newPath)) stylesheet.add(newPath);
    }

    public static void selectTheme(String theme) {
        Scene dashboardScene = Window.getWindowAt(Window.WINDOW_DASHBOARD).getStage().getScene();
        Scene settingsScene = Window.getWindowAt(Window.SETTINGS).getStage().getScene();

        replaceStylesheet(dashboardScene, "main", theme);
        replaceStylesheet(dashboardScene, "codegalaxy", theme);
        replaceStylesheet(settingsScene, "settings", theme);

        DashboardController controller = (DashboardController) Window.getController(Window.WINDOW_DASHBOARD);
        for (CodeGalaxy codeGalaxy : controller.getOpenCodeGalaxies()) {
            System.out.println("Setting highlighter CSS for: " + codeGalaxy.getFilePath());
            codeGalaxy.getHighlighter().setupHighlighterCSS();
        }
        controller.onThemeChanged();
    }

    private static void replaceStylesheet(Scene scene, String baseName, String theme) {
        String lightPath = toExternalForm("/fili5rovic/codegalaxy/" + baseName + "-light.css");
        String darkPath = toExternalForm("/fili5rovic/codegalaxy/" + baseName + "-dark.css");

        // Always remove both, add only the selected one
        scene.getStylesheets().removeAll(lightPath, darkPath);

        String selectedPath = theme.equals("light") ? lightPath : darkPath;
        scene.getStylesheets().add(selectedPath);
    }

    private static String toExternalForm(String name) {
        return Objects.requireNonNull(Main.class.getResource(name)).toExternalForm();
    }

    private static String toExternalFormNullable(String name) {
        URL resource = Main.class.getResource(name);
        return resource != null ? resource.toExternalForm() : null;
    }
}
