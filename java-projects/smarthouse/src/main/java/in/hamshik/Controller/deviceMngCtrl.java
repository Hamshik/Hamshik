package in.hamshik.Controller;

import in.hamshik.WifiHandler.WifiDisplayer;
import in.hamshik.WifiHandler.WifiSender;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class deviceMngCtrl extends Controller {
    @FXML private ProgressIndicator loadingSpinner;
    @FXML private Label statusLabel;
    @FXML public SplitPane root;
    @FXML private VBox deviceListContainer;
    
    private WifiDisplayer wifiDisplayer;

    @FXML protected void initialize() {
        super.initialize(); // Uses this screen's own fields (wifiBut, root, etc.)
        wifiBut.setOnAction(e -> wifiShow());
        wifiDisplayer = new WifiDisplayer(
            root,
            loadingSpinner,
            statusLabel,
            deviceListContainer,
            device -> {
                System.out.println("Selected ESP32: " + device.name() + " (" + device.host() + ")");
                setWifiButtonConnected(true);
                if (wifiBut != null) {
                    wifiBut.setText(device.name());
                }
                WifiSender.sendDataToESP32(device.host(), "hello from java");
            },
            () -> {
                System.out.println("ESP32 Disconnected");
                setWifiButtonConnected(false);
                if (wifiBut != null) {
                    wifiBut.setText("Disconnected");
                }
            }
        );
    }

    public void dispose() { wifiDisplayer.dispose(); }

    void setWifiButtonConnected(boolean connected){
        wifiBut.getStyleClass().removeAll("connected-btn", "disconnected-btn");
        wifiBut.getStyleClass().add(connected ? "connected-btn" : "disconnected-btn");
    }

    @FXML public void wifiShow() { wifiDisplayer.show(); }
}