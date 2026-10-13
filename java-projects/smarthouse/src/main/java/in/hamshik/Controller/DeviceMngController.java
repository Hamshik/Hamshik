package in.hamshik.Controller;

import in.hamshik.WifiHandler.WifiDisplayer;
import in.hamshik.WifiHandler.WifiSender;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class DeviceMngController extends Controller {
    @FXML private VBox modalCard;
    @FXML private ProgressIndicator loadingSpinner;
    @FXML private Label statusLabel;
    @FXML private VBox deviceListContainer;
    
    private WifiDisplayer wifiDisplayer;

    @FXML 
    protected  void initialize() {
        super.initialize(); // Injects shared layout and binds shared elements automatically
        
        wifiDisplayer = new WifiDisplayer(
            root, // Inherited from base Controller
            modalCard,
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

    public void dispose() {
        if (wifiDisplayer != null) {
            wifiDisplayer.dispose();
        }
    }

    void setWifiButtonConnected(boolean connected){
        if (wifiBut != null) {
            wifiBut.getStyleClass().removeAll("connected-btn", "disconnected-btn");
            wifiBut.getStyleClass().add(connected ? "connected-btn" : "disconnected-btn");
        }
    }

    @FXML 
    void wifiShow() { 
        if (wifiDisplayer != null) {
            wifiDisplayer.show(); 
        }
    }
}