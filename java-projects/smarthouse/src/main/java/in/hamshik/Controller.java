package in.hamshik;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Controller {
    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane mainContent;
    @FXML
    private Button wifiBut;

    // Modal elements from FXML
    @FXML
    private StackPane modalOverlay;
    @FXML
    private VBox modalCard;
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private Label statusLabel;
    @FXML
    private VBox deviceListContainer;

    private WifiDisplayer wifiDisplayer;

    @FXML
    public void initialize() {
        // Initialize dedicated Wi-Fi displayer
        wifiDisplayer = new WifiDisplayer(
                mainContent,
                modalOverlay,
                modalCard,
                loadingSpinner,
                statusLabel,
                deviceListContainer,
                device -> {
                    System.out.println("Selected ESP32: " + device.name() + " (" + device.host() + ")");
                    wifiBut.setText(device.name());
                },
                () -> {
                    System.out.println("ESP32 Disconnected");
                    wifiBut.setText("Disconnected");
                });
    }

    @FXML
    void wifiShow(ActionEvent e) { wifiDisplayer.show(); }

    @FXML
    void closeModal() { wifiDisplayer.hide(); }
}
