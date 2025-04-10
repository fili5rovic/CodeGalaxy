package fili5rovic.codegalaxy.util;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;


public class SVGHelper {

    public static class SVGIcon {

        private final SVGPath path;

        private SVGIcon(String svgContent, double size, Color color, double opacity) {
            this.path = (SVGPath) loadSVG(svgContent, size);
            this.path.setOpacity(opacity);
            if (color != null) {
                this.path.setFill(color);
            }
        }

        public Node getNode() {
            return path;
        }


        public void setColor(Color color) {
            path.setFill(color);
        }
    }

    public static Node loadSVG(String svgContent, double size) {
        SVGPath path = new SVGPath();
        path.setContent(svgContent);

        double scale = size / Math.max(path.getBoundsInLocal().getWidth(), path.getBoundsInLocal().getHeight());
        path.setScaleX(scale);
        path.setScaleY(scale);

        return path;
    }

    public static Node get(SVG svg, double size) {
        String icon = "";
        String color = "";
        double opacity = 1.0;
        switch (svg) {
            case FOLDER_EMPTY:
                opacity = 0.5;
            case FOLDER:
                icon = "M10 4H4C2.9 4 2 4.9 2 6V18C2 19.1 2.9 20 4 20H20C21.1 20 22 19.1 22 18V8C22 6.9 21.1 6 20 6H12L10 4Z";
                color = "#FFA000";
                break;
            case FILE:
                icon = "M6 2C5.45 2 5 2.45 5 3V21C5 21.55 5.45 22 6 22H18C18.55 22 19 21.55 19 21V8.83C19 8.3 18.79 7.79 18.41 7.41L13.59 2.59C13.21 2.21 12.7 2 12.17 2H6Z";
                color = "#FFFFFF";
                break;
            case JAVA_CLASS:
                icon = "M6 2L18 2L18 22L6 22Z M9 6H15V8H9V6Z M9 10H15V12H9V10Z";
                color = "#007ACC";
                break;
        }
        return new SVGIcon(icon, size, Color.web(color), opacity).getNode();
    }
}