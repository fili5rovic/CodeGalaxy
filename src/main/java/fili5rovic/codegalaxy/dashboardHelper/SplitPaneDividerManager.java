package fili5rovic.codegalaxy.dashboardHelper;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A static utility class to control user interaction with specific SplitPane dividers.
 * This allows programmatic control while disabling manual dragging by the user for
 * individual dividers.
 */
public final class SplitPaneDividerManager {

    // Private constructor to prevent instantiation.
    private SplitPaneDividerManager() {}

    /**
     * Prevents or allows the user from manually dragging a specific divider.
     * The program can still move the divider using setPosition().
     *
     * @param splitPane    The SplitPane to modify.
     * @param dividerIndex The index of the divider to lock/unlock.
     * @param locked       If true, the user cannot drag the specified divider. If false, they can.
     */
    public static void setDividerLocked(SplitPane splitPane, int dividerIndex, boolean locked) {
        if (splitPane == null) {
            throw new IllegalArgumentException("SplitPane cannot be null.");
        }

        // We use Platform.runLater to ensure this code runs after the UI has been
        // laid out and the skin has created the divider nodes. This avoids timing issues.
        Platform.runLater(() -> {
            // Find all divider nodes.
            List<Node> dividers = splitPane.lookupAll(".split-pane-divider")
                    .stream()
                    .collect(Collectors.toList()); // Collect to a modifiable list.

            // Check if the requested index is valid.
            if (dividerIndex < 0 || dividerIndex >= dividers.size()) {
                System.err.println("Error: Divider index " + dividerIndex + " is out of bounds.");
                return;
            }

            // Sort the dividers by their layout position to ensure correct order.
            // This is crucial because lookupAll does not guarantee order.
            Comparator<Node> comparator = (splitPane.getOrientation() == Orientation.HORIZONTAL)
                    ? Comparator.comparingDouble(n -> ((Region) n).getLayoutX())
                    : Comparator.comparingDouble(n -> ((Region) n).getLayoutY());

            dividers.sort(comparator);

            // Get the specific divider by its sorted index and apply the lock.
            Node targetDivider = dividers.get(dividerIndex);
            targetDivider.setVisible(!locked);
        });
    }

    /**
     * Sets the position of a specific divider programmatically.
     * This works regardless of whether the divider is locked for the user.
     *
     * @param splitPane    The SplitPane containing the divider.
     * @param dividerIndex The index of the divider to move.
     * @param position     The new position (a value between 0.0 and 1.0).
     */
    public static void setPosition(SplitPane splitPane, int dividerIndex, double position) {
        if (splitPane == null) {
            throw new IllegalArgumentException("SplitPane cannot be null.");
        }
        if (dividerIndex < 0 || dividerIndex >= splitPane.getDividers().size()) {
            throw new IllegalArgumentException("Invalid divider index for the given SplitPane: " + dividerIndex);
        }
        splitPane.getDividers().get(dividerIndex).setPosition(position);
    }
}