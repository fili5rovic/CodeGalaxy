package fili5rovic.codegalaxy.code.manager.codeActions.rightClick;

import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import org.eclipse.lsp4j.Location;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public class LocationsPopup extends Popup {

    private List<? extends Location> locations;
    private final VBox content;
    private Consumer<Location> onLocationSelected;
    private String title;

    public LocationsPopup() {
        super();
        setAutoHide(true);
        this.content = new VBox();
        this.content.getStyleClass().add("locations-popup");
        getContent().add(content);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocations(List<? extends Location> locations) {
        this.locations = locations;
    }

    public void setOnLocationSelected(Consumer<Location> onLocationSelected) {
        this.onLocationSelected = onLocationSelected;
    }

    public void updateContent() {
        content.getChildren().clear();

        Label titleLabel = new Label(title + " : " + locations.size());
        titleLabel.getStyleClass().add("locations-popup-title");
        content.getChildren().add(titleLabel);

        for (Location location : locations) {
            int line = location.getRange().getStart().getLine();
            URI uri = URI.create(location.getUri());
            String filename = Paths.get(uri).getFileName().toString();

            HBox item = new HBox();
            item.setSpacing(2);
            item.setAlignment(Pos.CENTER_LEFT);
            item.getStyleClass().add("location-item");

            Label filenameLabel = new Label(filename);
            filenameLabel.getStyleClass().add("location-filename");
            filenameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(filenameLabel, Priority.ALWAYS);
            filenameLabel.setGraphic(SVGUtil.getIconByPath(Path.of(uri), 14, 14, 0));

            Label lineLabel = new Label(":" + (line + 1));
            lineLabel.getStyleClass().add("location-line");

            item.getChildren().addAll(filenameLabel, lineLabel);
            item.setOnMouseClicked(_ -> {
                if (onLocationSelected != null) {
                    onLocationSelected.accept(location);
                }
                hide();
            });

            content.getChildren().add(item);
        }
    }

}
