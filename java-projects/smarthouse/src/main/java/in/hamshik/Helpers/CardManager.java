package in.hamshik.Helpers;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import in.hamshik.App;
import in.hamshik.Controller.CardCtrl.CardCtrl;
import in.hamshik.Controller.CardCtrl.CardShowerCtrl;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Manages IoT variable cards on the dashboard "board" (Pane).
 * Cards are loaded from cardShower.fxml.
 */
public class CardManager {
    public final Pane board;
    private final List<DeviceInfo> removedCards = new ArrayList<>();
    private final List<CardShowerCtrl> cardControllers = new ArrayList<>();
    private final Map<String, JsonElement> cachedValues = new HashMap<>();

    public record Range(int from, int to) {}
    public record DeviceInfo(String sensorName, String dataTy, 
        String varName, String permissions, Range range){}
    public record RemovedCardEntry(DeviceInfo info, double layoutX, double layoutY) {}

    private final Deque<RemovedCardEntry> undoStack = new ArrayDeque<>();
    private Runnable onUndoStateChanged;

    public CardCtrl cardCtrl;

    public CardManager(Pane board) {
        this.board = board;
    }

    public VBox addCard(DeviceInfo info) {
        try {
            FXMLLoader loader = App.createFXMLLoader("fxmlFiles/cardShower.fxml");
            VBox card = loader.load();
            CardShowerCtrl controller = loader.getController();
            controller.setup(info);
            controller.setOnDelete(this::removeCard);
            controller.setOnEdit(ctrl -> {
                if (App.dashCtrl != null) {
                    App.dashCtrl.openEditCardPopup(ctrl);
                }
            });

            cardControllers.add(controller);

            // Populate with last known cached value if available
            JsonElement cached = cachedValues.get(info.varName().trim().toLowerCase());
            if (cached != null) {
                controller.updateValue(cached);
            }

            positionCard(card);
            board.getChildren().add(card);
            return card;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cardShower.fxml", e);
        }
    }

    public void removeCard(CardShowerCtrl controller) {
        if (controller == null) return;
        double x = controller.getCardRoot().getLayoutX();
        double y = controller.getCardRoot().getLayoutY();
        undoStack.push(new RemovedCardEntry(controller.getInfo(), x, y));

        board.getChildren().remove(controller.getCardRoot());
        cardControllers.remove(controller);
        if (controller.getInfo() != null) {
            removedCards.add(controller.getInfo());
        }

        if (onUndoStateChanged != null) {
            onUndoStateChanged.run();
        }
    }

    public void undo() {
        if (undoStack.isEmpty()) return;
        RemovedCardEntry entry = undoStack.pop();
        VBox card = addCard(entry.info());
        card.setLayoutX(entry.layoutX());
        card.setLayoutY(entry.layoutY());
        DragHelper.makeDraggable(card, board);

        if (onUndoStateChanged != null) {
            onUndoStateChanged.run();
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public void setOnUndoStateChanged(Runnable onUndoStateChanged) {
        this.onUndoStateChanged = onUndoStateChanged;
    }

    public void reapplyCachedValue(CardShowerCtrl controller) {
        if (controller == null || controller.getInfo() == null) return;
        JsonElement cached = cachedValues.get(controller.getInfo().varName().trim().toLowerCase());
        if (cached != null) {
            controller.updateValue(cached);
        }
    }

    public void updateVariable(String varName, JsonElement value) {
        if (varName == null) return;
        cachedValues.put(varName.trim().toLowerCase(), value);

        Runnable task = () -> {
            for (CardShowerCtrl controller : cardControllers) {
                if (controller.getInfo() != null
                        && controller.getInfo().varName().trim().equalsIgnoreCase(varName.trim())) {
                    controller.updateValue(value);
                }
            }
        };

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    public void updateVariable(String varName, double value) {
        updateVariable(varName, new JsonPrimitive(value));
    }

    public List<DeviceInfo> getRemovedCards() {
        return removedCards;
    }

    public List<CardShowerCtrl> getCardControllers() {
        return cardControllers;
    }

    private void positionCard(VBox card) {
        final double cardWidth = card.getPrefWidth() > 0 ? card.getPrefWidth() : 340;
        final double cardGap = 16;
        int index = board.getChildren().size();
        int columns = Math.max(1, (int) (board.getWidth() / (cardWidth + cardGap)));
        card.setLayoutX(16 + (index % columns) * (cardWidth + cardGap));
        card.setLayoutY(16 + (index / columns) * 190);
    }

}
