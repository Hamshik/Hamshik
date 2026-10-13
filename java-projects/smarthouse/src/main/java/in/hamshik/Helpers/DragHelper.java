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
            var point = container.sceneToLocal(e.getSceneX(), e.getSceneY());
            offset[0] = point.getX() - node.getLayoutX();
            offset[1] = point.getY() - node.getLayoutY();
            node.toFront();
        });

        node.setOnMouseDragged(e -> {
            var point = container.sceneToLocal(e.getSceneX(), e.getSceneY());
            double x = point.getX() - offset[0];
            double y = point.getY() - offset[1];

            x = clamp(x, Math.max(0, container.getWidth() - node.getLayoutBounds().getWidth()));
            y = clamp(y, Math.max(0, container.getHeight() - node.getLayoutBounds().getHeight()));

            node.setLayoutX(x);
            node.setLayoutY(y);
        });
    }

    private static double clamp(double value, double max) {
        return Math.clamp(value, 0, max);
    }
}
