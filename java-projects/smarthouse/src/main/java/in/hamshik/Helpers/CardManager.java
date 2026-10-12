package in.hamshik.Helpers;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Manages IoT sensor cards on the dashboard "board" (Pane).
 * Each card is decorated with an Arduino Uno style PCB graphic.
 */
public class CardManager {
    private final Pane board;
    private final List<VBox> removedCards = new ArrayList<>();

    private static final double CARD_W = 230;

    public CardManager(Pane board) {
        this.board = board;
        
    }

    /* ================= Card creation ================= */
    public VBox createIoTSensorCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(CARD_W);

        card.getChildren().addAll(
                createSensorDropdown()
        );
        return card;
    }

    /* ================= Functional parts ================= */
    private ComboBox<String> createSensorDropdown() {
        ComboBox<String> sensorNameCombo = new ComboBox<>();
        sensorNameCombo.getItems().addAll("DHT11 (Temp/Hum)", "Ultrasonic (HC-SR04)", "BMP280 (Pressure)",
                "MQ-2 (Gas)");
        sensorNameCombo.setPromptText("Select Sensor");
        sensorNameCombo.setMaxWidth(Double.MAX_VALUE);
        sensorNameCombo.getStyleClass().add("dropdown-menu");
        return sensorNameCombo;
    }
    /* ================= Board management ================= */

    public void addNewCard() {
        VBox newCard = createIoTSensorCard();
        addCard(newCard);
        DragHelper.makeDraggable(board.getChildren().get(board.getChildren().size() - 1), newCard);
    }

    private void addCard(VBox card) {
        int index = board.getChildren().size();

        double x = 20 + (index * 36) % Math.max(40, board.getWidth()  - CARD_W - 40);
        double y = 20 + (index * 36) % Math.max(40, board.getHeight() - 300);

        card.setLayoutX(Math.max(10, x));
        card.setLayoutY(Math.max(10, y));

        board.getChildren().add(card);
        DragHelper.makeDraggable(card, board);

        Platform.runLater(() -> {
            double maxX = Math.max(0, board.getWidth() - card.getWidth());
            double maxY = Math.max(0, board.getHeight() - card.getHeight());
            card.setLayoutX(Math.min(card.getLayoutX(), maxX));
            card.setLayoutY(Math.min(card.getLayoutY(), maxY));
        });
    }

    public void deleteCard(VBox card) {
        if (board.getChildren().remove(card))
            removedCards.add(card);
    }

    public void undoDelete() {
        VBox restoredCard = getLastRemovedCard();
        if (restoredCard != null)
            addCard(restoredCard);
    }

    private VBox getLastRemovedCard() {
        if (removedCards.isEmpty())
            return null;

        return removedCards.remove(removedCards.size() - 1);
    }
}
