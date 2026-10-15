package in.hamshik.WifiHandler;

import in.hamshik.DNSHandler.*;
import in.hamshik.DNSHandler.WiFiScanner.EspDevice;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/** Coordinates the Wi-Fi device list, connection state, and scanner lifecycle. */
public class WifiDisplayer {
    private final Node backgroundNode;
    private final ProgressIndicator loadingSpinner;
    private final Label statusLabel;
    private final WifiDeviceListView deviceListView;
    private final Consumer<EspDevice> onDeviceConnected;
    private final Runnable onDeviceDisconnected;
    private final WiFiScanner wifiScanner = new WiFiScanner();
    private final GaussianBlur blurEffect = new GaussianBlur(15);
    private String connectedDeviceName;

    public WifiDisplayer(Node backgroundNode,
                         ProgressIndicator loadingSpinner, Label statusLabel,
                         VBox deviceListContainer, Consumer<EspDevice> onDeviceConnected,
                         Runnable onDeviceDisconnected) {
        this.backgroundNode = backgroundNode;
        this.loadingSpinner = loadingSpinner;
        this.statusLabel = statusLabel;
        this.onDeviceConnected = onDeviceConnected;
        this.onDeviceDisconnected = onDeviceDisconnected;
        this.deviceListView = new WifiDeviceListView(deviceListContainer, statusLabel,
                this::handleDeviceConnection, device -> disconnect());
    }

    /** Starts scanning and displays discovered devices in the device list. */
    public void show() {
        endScanning();
        resetDeviceListState();
        beginScanning();
    }

    /** Stops scanning while leaving the current device list visible. */
    public void hide() {
        removeBackgroundBlur();
        loadingSpinner.setVisible(false);
        endScanning();
    }

    public void dispose() {
        removeBackgroundBlur();
        endScanning();
    }

    public void disconnect() {
        if (connectedDeviceName == null) return;
        connectedDeviceName = null;
        if (onDeviceDisconnected != null) onDeviceDisconnected.run();
        refreshDeviceList();
    }

    public boolean isConnected() { return connectedDeviceName != null; }
    public String getConnectedDeviceName() { return connectedDeviceName; }

    public void setConnectedDevice(String name) {
        connectedDeviceName = name;
        refreshDeviceList();
    }

    public void applyBackgroundBlur() {
        if (backgroundNode != null) backgroundNode.setEffect(blurEffect);
    }

    private void removeBackgroundBlur() {
        if (backgroundNode != null) backgroundNode.setEffect(null);
    }

    private void resetDeviceListState() {
        deviceListView.clear();
        loadingSpinner.setVisible(true);
        statusLabel.setText("Scanning local network for ESP32...");
    }

    private void beginScanning() {
        loadingSpinner.setVisible(true);
        wifiScanner.startScanning(devices ->
                Platform.runLater(() -> {
                    if (devices.isEmpty()) {
                        loadingSpinner.setVisible(true);
                    } else {
                        loadingSpinner.setVisible(false);
                    }
                    deviceListView.render(devices, connectedDeviceName);
                }), name -> Platform.runLater(() -> {
                    if (connectedDeviceName != null && name.equalsIgnoreCase(connectedDeviceName)) {
                        disconnect();
                    } else {
                        refreshDeviceList();
                    }
                }));
    }

    private void endScanning() { wifiScanner.stopScanning(); }

    private void refreshDeviceList() {
        deviceListView.render(wifiScanner.getDevices(), connectedDeviceName);
    }

    private void handleDeviceConnection(EspDevice device) {
        connectedDeviceName = device.name();
        if (onDeviceConnected != null) onDeviceConnected.accept(device);
        refreshDeviceList();
        hide();
    }
}
