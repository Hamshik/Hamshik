package in.hamshik.Controller;

import in.hamshik.Helpers.CardManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class DashController extends Controller {
    @FXML private Pane board;
    @FXML private StackPane modalOverlay;

    private CardManager cardManager;

    @FXML 
    protected void initialize() {
        super.initialize(); // Injects shared layout and binds shared elements automatically
        cardManager = new CardManager(board);
    }

    @FXML 
    private void addNewCard(ActionEvent event) {
        cardManager.addNewCard();
    }
}