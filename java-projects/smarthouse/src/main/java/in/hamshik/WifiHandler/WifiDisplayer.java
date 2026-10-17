package in.hamshik.WifiHandler;

import in.hamshik.DNSHandler.*;
import in.hamshik.DNSHandler.WiFiScanner.EspDevice;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

/** Coordinates the Wi-Fi device list, connection state, and scanner lifecycle. */
public class WifiDisplayer {
    private final ProgressIndicator loadingSpinner;
    private final Label statusLabel;
    private final WifiDeviceListView deviceListView;
    private final Consumer<EspDevice> onDeviceConnected;
    private final Runnable onDeviceDisconnected;
    private final WiFiScanner wifiScanner = new WiFiScanner();
    private String connectedDeviceName;
    private String connectingDeviceName;
    private EspDevice manualDevice;
    private List<EspDevice> discoveredDevices = List.of();

    public WifiDisplayer( ProgressIndicator loadingSpinner, Label statusLabel,
                         VBox deviceListContainer, Consumer<EspDevice> onDeviceConnected,
                         Runnable onDeviceDisconnected) {
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
        loadingSpinner.setVisible(false);
        endScanning();
    }

    public void dispose() {
        endScanning();
    }

    public void disconnect() {
        if (connectedDeviceName == null && connectingDeviceName == null) return;
        connectedDeviceName = null;
        connectingDeviceName = null;
        if (onDeviceDisconnected != null) onDeviceDisconnected.run();
        refreshDeviceList();
    }

    /** Marks the pending connection as established and refreshes its card action. */
    public void markConnected() {
        connectedDeviceName = connectingDeviceName;
        connectingDeviceName = null;
        refreshDeviceList();
    }

    /** Clears the pending state after a failed connection attempt. */
    public void connectionFailed() {
        connectedDeviceName = null;
        connectingDeviceName = null;
        refreshDeviceList();
    }

    public boolean isConnected() { return connectedDeviceName != null; }
    public String getConnectedDeviceName() { return connectedDeviceName; }
    public String getConnectingDeviceName() { return connectingDeviceName; }

    public void setConnectedDevice(String name) {
        connectedDeviceName = name;
        connectingDeviceName = null;
        refreshDeviceList();
    }

    /** Connects to a manually entered device and keeps it visible in the device list. */
    public void connectManually(EspDevice device) {
        manualDevice = device;
        connectingDeviceName = device.name();
        connectedDeviceName = null;
        if (onDeviceConnected != null) onDeviceConnected.accept(device);
        refreshDeviceList();
        hide();
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
                    discoveredDevices = devices;
                    renderDevices();
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
        discoveredDevices = wifiScanner.getDevices();
        renderDevices();
    }

    private void renderDevices() {
        List<EspDevice> devices = new ArrayList<>(discoveredDevices);
        if (manualDevice != null && devices.stream().noneMatch(device ->
                device.host().equalsIgnoreCase(manualDevice.host()) && device.port() == manualDevice.port())) {
            devices.add(manualDevice);
        }
        deviceListView.render(devices, connectingDeviceName, connectedDeviceName);
    }

    private void handleDeviceConnection(EspDevice device) {
        connectingDeviceName = device.name();
        connectedDeviceName = null;
        if (onDeviceConnected != null) onDeviceConnected.accept(device);
        refreshDeviceList();
        hide();
    }
}
