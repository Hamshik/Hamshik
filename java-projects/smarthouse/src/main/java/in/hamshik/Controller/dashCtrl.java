package in.hamshik.Controller;

import in.hamshik.Helpers.CardManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class dashCtrl extends Controller {
    @FXML private Pane board;
    @FXML private StackPane modalOverlay;
    @FXML public SplitPane root;

    private CardManager cardManager;

    @FXML protected void initialize() {
        super.initialize(); // Automatically runs parent initialize with this screen's injected items
        cardManager = new CardManager(board);
    }

    @FXML 
    private void addNewCard(ActionEvent event) {
        cardManager.addNewCard();
    }
}