package in.hamshik.Controller;

import in.hamshik.DNSHandler.Esp32WebSocketClient;
import in.hamshik.DNSHandler.Esp32WebSocketClient.DeviceListener;
import in.hamshik.WifiHandler.WifiDisplayer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class deviceMngCtrl extends Controller {
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private Label statusLabel;
    @FXML
    public SplitPane root;
    @FXML
    private VBox deviceListContainer;

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
                if (wifiBut != null)
                    wifiBut.setText(device.name());
                Esp32WebSocketClient.connect(device, getDeviceListener());
            },
            () -> {
                System.out.println("ESP32 Disconnected");
                setWifiButtonConnected(false);
                if (wifiBut != null)
                    wifiBut.setText("Disconnected");
                Esp32WebSocketClient.disconnect();
            }
        );
    }

    public void dispose() {
        wifiDisplayer.dispose();
        Esp32WebSocketClient.disconnect();
    }

    void setWifiButtonConnected(boolean connected) {
        wifiBut.getStyleClass().removeAll("connected-btn", "disconnected-btn");
        wifiBut.getStyleClass().add(connected ? "connected-btn" : "disconnected-btn");
    }

    @FXML
    public void wifiShow() {
        wifiDisplayer.show();
    }

    DeviceListener getDeviceListener(){
        return new Esp32WebSocketClient.DeviceListener() {
            @Override
            public void onConnectingState(){
                wifiBut.setText("Connecting");
                loadingSpinner.setVisible(true);
            }

            @Override
            public void onConnected() {
                setWifiButtonConnected(true);
                wifiBut.setText(wifiDisplayer.getConnectedDeviceName());
                loadingSpinner.setVisible(false);
            }

            @Override 
            public void onFailedConnection(){
                loadingSpinner.setVisible(false);
                wifiDisplayer.disconnect();
                setWifiButtonConnected(false);
                wifiBut.setText("Disconnected");
                statusLabel.setText("Connection timed out. Check the ESP32 and try again.");
            }

            @Override 
            public void onDisconnectedCallback() {
                loadingSpinner.setVisible(false);
                wifiDisplayer.disconnect();
                setWifiButtonConnected(false);
                wifiBut.setText("Disconnected");
                statusLabel.setText("ESP32 connection lost.");
            }
        };
    }
}