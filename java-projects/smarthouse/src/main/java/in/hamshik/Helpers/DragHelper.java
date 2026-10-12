package in.hamshik.Helpers;

import javafx.scene.Node;
import javafx.scene.layout.Region;

public class DragHelper {

    /**
     * Makes a node draggable, clamped inside the given container.
     *
     * @param node      the node to drag (from your controller's @FXML field)
     * @param container the area it must stay inside (e.g. a Pane from FXML)
     */
    public static void makeDraggable(Node node, Region container) {
        final double[] offset = new double[2];

        node.setOnMousePressed(e -> {
            offset[0] = e.getSceneX() - node.getLayoutX();
            offset[1] = e.getSceneY() - node.getLayoutY();
            node.toFront();
        });

        node.setOnMouseDragged(e -> {
            double x = e.getSceneX() - offset[0];
            double y = e.getSceneY() - offset[1];

            x = clamp(x, 0, container.getWidth() - node.getLayoutBounds().getWidth());
            y = clamp(y, 0, container.getHeight() - node.getLayoutBounds().getHeight());

            node.setLayoutX(x);
            node.setLayoutY(y);
        });
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
