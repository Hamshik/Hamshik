package in.hamshik.Controller;

import in.hamshik.Helpers.CardManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.util.Collection;

public class DashCtrl extends Controller {
    @FXML private Pane board;
    @FXML private StackPane modalOverlay;
    @FXML public SplitPane root;

    public CardManager cardManager;
    @FXML private CardController cardRootController;

    @FXML protected void initialize() {
        super.initialize(); // Automatically runs parent initialize with this screen's injected items
        cardManager = new CardManager(board);
        cardRootController.setOnDismiss(this::closeAddCardPopup);
    }

    @FXML 
    private void addNewCard(ActionEvent event) {
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    private void closeAddCardPopup() {
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
    }

    public void setVariableNames(Collection<String> names) {
        cardRootController.setVariableNames(names);
    }

    @FXML
    private void closeCard(MouseEvent event) {
        if (event.getTarget() == modalOverlay)
            closeAddCardPopup();
    }
}
