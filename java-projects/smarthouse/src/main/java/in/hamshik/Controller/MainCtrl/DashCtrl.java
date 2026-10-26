package in.hamshik.Controller.MainCtrl;

import in.hamshik.Controller.CardCtrl.CardCtrl;
import in.hamshik.Controller.CardCtrl.CardShowerCtrl;
import in.hamshik.Helpers.CardManager;
import in.hamshik.Helpers.VarHelpers.Variable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import com.google.gson.JsonElement;
import java.util.Collection;

public class DashCtrl extends Controller {
    @FXML private Pane board;
    @FXML private StackPane modalOverlay;
    @FXML public SplitPane root;
    @FXML private Button undoBtn;

    public CardManager cardManager;
    @FXML private CardCtrl cardRootController;

    @FXML protected void initialize() {
        super.initialize(); // Automatically runs parent initialize with this screen's injected items
        cardManager = new CardManager(board);
        cardRootController.setOnDismiss(this::closeAddCardPopup);

        cardManager.setOnUndoStateChanged(() -> {
            if (undoBtn != null) {
                undoBtn.setDisable(!cardManager.canUndo());
            }
        });
        if (undoBtn != null) {
            undoBtn.setDisable(true);
        }
    }

    @FXML 
    private void addNewCard(ActionEvent event) {
        cardRootController.resetToNew();
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    @FXML
    private void undoCard(ActionEvent event) {
        cardManager.undo();
    }

    public void openEditCardPopup(CardShowerCtrl cardToEdit) {
        cardRootController.startEdit(cardToEdit);
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    private void closeAddCardPopup() {
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
        cardRootController.resetToNew();
    }

    public void setVariableNames(Collection<Variable> names) {
        cardRootController.setVariableNames(names);
    }

    public void updateVariable(String varName, JsonElement value) {
        if (cardManager != null) {
            cardManager.updateVariable(varName, value);
        }
    }

    @FXML
    private void closeCard(MouseEvent event) {
        if (event.getTarget() == modalOverlay)
            closeAddCardPopup();
    }
}
