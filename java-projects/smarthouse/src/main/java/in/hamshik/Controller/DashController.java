package in.hamshik.Controller;

import in.hamshik.Helpers.CardManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class DashController extends Controller {
    @FXML private Pane board;
    @FXML private SplitPane root;
    @FXML private AnchorPane pageContent;
    @FXML private StackPane modalOverlay;

    private CardManager cardManager;

    @FXML private void initialize() {
        cardManager = new CardManager(board);
    }

    @FXML private void addNewCard(ActionEvent event) {
        cardManager.addNewCard();
    }
}
