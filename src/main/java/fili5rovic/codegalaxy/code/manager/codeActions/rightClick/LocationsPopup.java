package fili5rovic.codegalaxy.code.manager.codeActions.rightClick;

import fili5rovic.codegalaxy.util.SVGUtil;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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
        titleLabel.setStyle("-fx-font-size: 18px; -fx-padding: 2px");
        content.getChildren().add(titleLabel);

        for (Location location : locations) {
            int line = location.getRange().getStart().getLine();
            URI uri = URI.create(location.getUri());
            String filename = Paths.get(uri).getFileName().toString();

            Label item = new Label(filename + " :" + (line + 1));
            item.setGraphic(SVGUtil.getIconByPath(Path.of(uri), 16, 16, 0));
            item.setStyle("-fx-cursor: hand; -fx-padding: 5;-fx-font-size: 18px"); // hardcoded for now
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
