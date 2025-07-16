package fili5rovic.codegalaxy.util;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class SVGUtil {

    private static final String BASE_FOLDER = "fili5rovic/codegalaxy/svg/";
    private static final String DEFAULT_ICON_PATH = "tip.svg";

    public static ImageView getIconByPath(Path path, double width, double height, int translateY) {
        String iconName;

        if (Files.isDirectory(path)) {
            File[] files = path.toFile().listFiles();
            if (files == null || files.length == 0) {
                iconName = "folderEmpty";
            } else if(Objects.equals(MetaDataHelper.getOutputPath(), path.toString())) {
                iconName = "folder-output";
            } else if (Objects.equals(MetaDataHelper.getSrcPath(), path.toString())) {
                iconName = "folder-src";
            } else {
                iconName = "folder";
            }
        } else if (path.toString().endsWith(".java")) {
            iconName = "java";
        } else if (path.toString().endsWith(".class")) {
            iconName = "class";
        } else if (path.toString().endsWith(".gitignore")) {
            iconName = "gitignore";
        } else if (path.toString().endsWith(".json")) {
            iconName = "json";
        } else {
            iconName = "file";
        }

        return getSVG("hierarchy/" + iconName + ".svg", width, height, translateY);
    }

    public static ImageView getUI(String name, double width, double height) {
        return getSVG("ui/" + name + ".svg", width, height, 0);
    }

    public static ImageView getEmoji(String name, double width, double height) {
        return getSVG("emoji/" + name + ".svg", width, height, 0);
    }

    public static ImageView getIcon(String name, double width, double height) {
        return getSVG("icon/" + name + ".svg", width, height, 0);
    }

    public static ImageView getCompletionIcon(String name, double width, double height) {
        return getSVG("completion/" + name + ".svg", width, height, 2);
    }

    private static ImageView getSVG(String relativePath, double width, double height, int translateY) {
        try {
            URL svgUrl = getResourceURL(relativePath);
            if (svgUrl == null) {
                System.err.println("SVG not found: " + relativePath + " — falling back to " + DEFAULT_ICON_PATH);
                svgUrl = getResourceURL(DEFAULT_ICON_PATH);
                if (svgUrl == null) {
                    System.err.println("Default SVG not found: " + DEFAULT_ICON_PATH);
                    return new ImageView();
                }
            }

            String content = new String(svgUrl.openStream().readAllBytes());
            if (!CSSUtil.isDarkTheme()) {
                content = content.replace("fill=\"#fff\"", "fill=\"#000\"");
                content = content.replace("stroke=\"#fff\"", "stroke=\"#000\"");
            }
            SVGUniverse universe = new SVGUniverse();
            URI uri = universe.loadSVG(new java.io.StringReader(content), relativePath);

            SVGDiagram diagram = universe.getDiagram(uri);

            BufferedImage bufferedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.translate(0, translateY);

            diagram.setIgnoringClipHeuristic(true);
            diagram.render(g2d);
            g2d.dispose();

            return new ImageView(SwingFXUtils.toFXImage(bufferedImage, null));

        } catch (SVGException e) {
            System.err.println("Failed to render SVG: " + e.getMessage());
            return new ImageView();
        } catch (IOException e) {
            System.err.println("Failed to read SVG file:" + e.getMessage());
            return new ImageView();
        }
    }

    private static URL getResourceURL(String relativePath) {
        return SVGUtil.class.getResource("/" + BASE_FOLDER + relativePath);
    }

}
