package in.hamshik.Controller;

import in.hamshik.WifiHandler.WifiDisplayer;
import in.hamshik.WifiHandler.WifiSender;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class DeviceMngController extends  Controller {
    @FXML private VBox modalCard;
    @FXML private ProgressIndicator loadingSpinner;
    @FXML private Label statusLabel;
    @FXML private VBox deviceListContainer;
    @FXML private SplitPane pageContent;
    
    private WifiDisplayer wifiDisplayer;

    @FXML private void initialize() {
        wifiDisplayer = new WifiDisplayer(
            pageContent,
            modalCard,
            loadingSpinner,
            statusLabel,
            deviceListContainer,
            device -> {
                System.out.println("Selected ESP32: " + device.name() + " (" + device.host() + ")");
                setWifiButtonConnected(true);
                wifiBut.setText(device.name());
                WifiSender.sendDataToESP32(device.host(), "hello from java");
            },
            () -> {
                System.out.println("ESP32 Disconnected");
                setWifiButtonConnected(false);
                wifiBut.setText("Disconnected");
            }
        );
        wifiDisplayer.show();
        
    }

    void setWifiButtonConnected(boolean connected){
        wifiBut.getStyleClass().removeAll("connected-btn", "disconnected-btn");
        wifiBut.getStyleClass().add(connected ? "connected-btn" : "disconnected-btn");

    }

    void wifiShow() {
        wifiDisplayer.show();
    }
}
