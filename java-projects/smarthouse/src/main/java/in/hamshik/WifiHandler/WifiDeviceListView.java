package in.hamshik.WifiHandler;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;
import in.hamshik.DNSHandler.WiFiScanner.EspDevice;

/** Presents discovered Wi-Fi devices and their connection actions. */
final class WifiDeviceListView {
    private final VBox deviceListContainer;
    private final Label statusLabel;
    private final Consumer<EspDevice> onConnect;
    private final Consumer<EspDevice> onDisconnect;

    WifiDeviceListView(VBox deviceListContainer, Label statusLabel,
                       Consumer<EspDevice> onConnect, Consumer<EspDevice> onDisconnect) {
        this.deviceListContainer = deviceListContainer;
        this.statusLabel = statusLabel;
        this.onConnect = onConnect;
        this.onDisconnect = onDisconnect;
    }

    void clear() { deviceListContainer.getChildren().clear(); }

    void render(List<EspDevice> devices, String connectingDeviceName, String connectedDeviceName) {
        deviceListContainer.getChildren().clear();
        statusLabel.setText(connectingDeviceName != null
                ? "Connecting to " + connectingDeviceName + "..."
                : connectedDeviceName != null
                ? "Connected to " + connectedDeviceName
                : devices.isEmpty()
                        ? "Scanning... No devices found yet."
                        : "Found " + devices.size() + " device(s):");
        for (EspDevice device : devices) {
            boolean connecting = device.name().equals(connectingDeviceName);
            boolean connected = device.name().equals(connectedDeviceName);
            deviceListContainer.getChildren().add(createDeviceCard(device, connecting, connected));
        }
    }

    private HBox createDeviceCard(EspDevice device, boolean connecting, boolean connected) {
        HBox card = new HBox(10);
        card.getStyleClass().add("device-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(createDeviceInfo(device), createActionButton(device, connecting, connected));
        return card;
    }

    private VBox createDeviceInfo(EspDevice device) {
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(device.name());
        name.getStyleClass().add("device-name");
        name.setWrapText(true);
        Label address = new Label("Host: " + device.name() + " • IP: "
                + device.host() + ":" + device.port() + " • " + device.type());
        address.getStyleClass().add("device-host");
        address.setWrapText(true);
        info.getChildren().addAll(name, address);
        return info;
    }

    private Button createActionButton(EspDevice device, boolean connecting, boolean connected) {
        Button button = new Button(connecting ? "Connecting..." : connected ? "Disconnect" : "Connect");
        button.getStyleClass().add(connected ? "disconnect-btn" : "connect-btn");
        button.setDisable(connecting);
        button.setOnAction(event -> {
            if (connected) onDisconnect.accept(device);
            else if (!connecting) onConnect.accept(device);
        });
        return button;
    }
}
