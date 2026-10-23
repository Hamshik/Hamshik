package in.hamshik.Controller;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import in.hamshik.App;
import in.hamshik.DNSHandler.Esp32WebSocketClient;
import in.hamshik.DNSHandler.Esp32WebSocketClient.DeviceListener;
import in.hamshik.WifiHandler.WifiDisplayer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DeviceMngCtrl extends Controller {
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private Label statusLabel;
    @FXML
    public SplitPane root;
    @FXML
    private VBox deviceListContainer;
    @FXML
    private StackPane modalOverlay;
    @FXML
    private ManualConnectionCardCtrl cardRootController;

    private WifiDisplayer wifiDisplayer;

    @FXML
    protected void initialize() {
        super.initialize(); // Uses this screen's own fields (wifiBut, root, etc.)
        wifiBut.setOnAction(e -> wifiShow());
        cardRootController.setOnConnect(device -> {
            statusLabel.setText("Connecting to " + device.host() + "...");
            wifiDisplayer.connectManually(device);
        });
        cardRootController.setOnDismiss(this::closeManualConnection);
        wifiDisplayer = new WifiDisplayer(
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
                    App.fxmlPipeline.setWifiButtonConnected(false);
                    if (wifiBut != null)
                        wifiBut.setText("Disconnected");
                    App.dashCtrl.setVariableNames(List.of());
                    Esp32WebSocketClient.disconnect();
                });
    }

    public void dispose() {
        wifiDisplayer.dispose();
        Esp32WebSocketClient.disconnect();
    }

    @FXML
    public void wifiShow() {
        wifiDisplayer.show();
    }

    @FXML
    private void showManualConnection() {
        wifiDisplayer.hide();
        statusLabel.setText("Enter the ESP32 IP address to connect manually.");
        modalOverlay.setManaged(true);
        modalOverlay.setVisible(true);
    }

    private void closeManualConnection() {
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
    }

    @FXML
    private void closeCard(MouseEvent event) {
        if (event.getTarget() == modalOverlay)
            closeManualConnection();
    }

    DeviceListener getDeviceListener() {
        return new Esp32WebSocketClient.DeviceListener() {
            @Override
            public void onConnectingState() {
                wifiBut.setText("Connecting");
                loadingSpinner.setVisible(true);
                statusLabel.setText("Connecting to " + wifiDisplayer.getConnectingDeviceName() + "...");
                App.dashCtrl.setVariableNames(List.of());
            }

            @Override
            public void onConnected() {
                wifiDisplayer.markConnected();
                App.fxmlPipeline.setWifiButtonConnected(true);
                App.fxmlPipeline.setWifiButText(wifiDisplayer.getConnectedDeviceName());
                loadingSpinner.setVisible(false);
                statusLabel.setText("Connected to " + wifiDisplayer.getConnectedDeviceName());
            }

            @Override
            public void onVariablesReceived(JsonObject response) {
                List<String> names = readVariableNames(response);
                App.dashCtrl.setVariableNames(names);
                System.out.println("Loaded " + names.size() + " ESP32 variable names.");
            }

            @Override
            public void onFailedConnection() {
                loadingSpinner.setVisible(false);
                wifiDisplayer.connectionFailed();
                App.fxmlPipeline.setWifiButtonConnected(false);
                App.fxmlPipeline.setWifiButText("Disconnected");
                statusLabel.setText("Connection timed out. Check the ESP32 and try again.");
                App.dashCtrl.setVariableNames(List.of());
            }

            @Override
            public void onDisconnectedCallback() {
                loadingSpinner.setVisible(false);
                wifiDisplayer.disconnect();
                App.fxmlPipeline.setWifiButtonConnected(false);
                App.fxmlPipeline.setWifiButText("Disconnected");
                statusLabel.setText("ESP32 connection lost.");
                App.dashCtrl.setVariableNames(List.of());
            }
        };
    }

    private List<String> readVariableNames(JsonObject response) {
        if (response == null)
            return List.of();

        JsonObject variables = null;
        for (var entry : response.entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase("Variables")
                    && entry.getValue().isJsonObject()) {
                variables = entry.getValue().getAsJsonObject();
                break;
            }
        }

        if (variables == null)
            return List.of();

        List<String> names = new ArrayList<>();
        for (var variable : variables.entrySet()) 
            names.add(variable.getKey());
        
        return names;
    }
}
