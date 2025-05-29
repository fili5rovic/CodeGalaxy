package fili5rovic.codegalaxy.code.manager.codeActions.rightClick;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.eclipse.lsp4j.Location;

import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public class LocationsPopup extends Popup {

    private List<? extends Location> locations;
    private final VBox content;
    private Consumer<Location> onLocationSelected;

    public LocationsPopup() {
        super();
        setAutoHide(true);
        this.content = new VBox();
        getContent().add(content);
    }

    public void setLocations(List<? extends Location> locations) {
        this.locations = locations;
    }

    public void setOnLocationSelected(Consumer<Location> onLocationSelected) {
        this.onLocationSelected = onLocationSelected;
    }

    public void updateContent() {
        content.getChildren().clear();

        content.getChildren().add(new Label(locations.size() + " locations"));

        for (Location location : locations) {
            int line = location.getRange().getStart().getLine();
            URI uri = URI.create(location.getUri());
            String filename = Paths.get(uri).getFileName().toString();

            Label item = new Label(filename + " at line " + (line + 1));
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
