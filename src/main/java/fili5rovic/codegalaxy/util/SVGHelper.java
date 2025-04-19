package fili5rovic.codegalaxy.util;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.eclipse.lsp4j.CompletionItemKind;


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
                icon = "M13.825 10.605V5.393a1.282 1.287 0 0 0-.647-1.116l-4.53-2.605a1.307 1.313 0 0 0-1.295 0L2.822 4.277c-.4.23-.647.657-.647 1.117v5.212c0 .46.246.886.647 1.116l4.53 2.605a1.307 1.313 0 0 0 1.295 0l4.531-2.605c.4-.231.647-.657.647-1.117M8 14.5V8m0 0l5.65-3.276m-11.3 0L8 8";
                color = "#007ACC";
                break;
        }
        return new SVGIcon(icon, size, Color.web(color), opacity).getNode();
    }

    public static Node getForKind(CompletionItemKind kind, double size) {
        String icon = "";
        String color = "";
        double opacity = 0.5;
        switch (kind) {
            case Field:
                icon = "M419.744,0.001H74.074C33.162,0.001,0,33.161,0,74.071v345.674c0,40.906,33.162,74.07,74.074,74.07h345.67 c40.91,0,74.072-33.164,74.072-74.07V74.071C493.816,33.161,460.654,0.001,419.744,0.001z M234.609,232.104 c13.488,0,24.418,10.929,24.418,24.433c0,13.483-10.93,24.416-24.418,24.416h-70.888v76.596c0,16.639-13.488,30.126-30.122,30.126 c-16.652,0-30.143-13.487-30.143-30.142V146.809c0-13.599,11.029-24.626,24.625-24.626h113.023 c13.596,0,24.627,11.027,24.627,24.626c0,13.599-11.031,24.628-24.627,24.628h-77.384v60.667H234.609z M378.016,357.548 c0,16.639-12.721,30.126-28.406,30.126c-15.674,0-28.389-13.487-28.389-30.126V152.309c0-16.638,12.715-30.126,28.389-30.126 c15.685,0,28.406,13.488,28.406,30.126V357.548z";
                color = "#FFA000";
                break;
            case Variable:
                icon = "M23.474,0.159L17.08,0.775c-0.406,0.039-0.844,0.383-0.978,0.768l-4.092,11.749L7.898,1.542 C7.764,1.158,7.325,0.814,6.92,0.775L0.526,0.159C0.121,0.12-0.096,0.399,0.041,0.783L8.085,23.15 c0.138,0.383,0.581,0.695,0.988,0.695h6.223h0.039c0.073,0,0.134-0.02,0.179-0.055c0.124-0.062,0.231-0.169,0.275-0.292 l0.039-0.108l8.13-22.607C24.096,0.399,23.879,0.12,23.474,0.159z";
                color = "#0000BB";
                break;
            default:
                icon = "M6 2C5.45 2 5 2.45 5 3V21C5 21.55 5.45 22 6 22H18C18.55 22 19 21.55 19 21V8.83C19 8.3 18.79 7.79 18.41 7.41L13.59 2.59C13.21 2.21 12.7 2 12.17 2H6Z";
                color = "#FFFFFF";
                break;

        }
        return new SVGIcon(icon, size, Color.web(color), opacity).getNode();
    }
}