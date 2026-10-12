package in.hamshik.WifiHandler;

import in.hamshik.DNSHandler.*;
import in.hamshik.DNSHandler.WiFiScanner.EspDevice;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.function.Consumer;

/** Coordinates the Wi-Fi scan modal, connection state, and scanner lifecycle. */
public class WifiDisplayer {
    private final Node backgroundNode;
    private final StackPane modalOverlay = new StackPane();
    private final VBox modalCard;
    private final ProgressIndicator loadingSpinner;
    private final Label statusLabel;
    private final WifiDeviceListView deviceListView;
    private final Consumer<EspDevice> onDeviceConnected;
    private final Runnable onDeviceDisconnected;
    private final WiFiScanner wifiScanner = new WiFiScanner();
    private final GaussianBlur blurEffect = new GaussianBlur(15);
    private String connectedDeviceName;

    public WifiDisplayer(Node backgroundNode, VBox modalCard,
                         ProgressIndicator loadingSpinner, Label statusLabel,
                         VBox deviceListContainer, Consumer<EspDevice> onDeviceConnected,
                         Runnable onDeviceDisconnected) {
        this.backgroundNode = backgroundNode;
        this.modalCard = modalCard;
        this.loadingSpinner = loadingSpinner;
        this.statusLabel = statusLabel;
        this.onDeviceConnected = onDeviceConnected;
        this.onDeviceDisconnected = onDeviceDisconnected;
        this.deviceListView = new WifiDeviceListView(deviceListContainer, statusLabel,
                this::handleDeviceConnection, device -> disconnect());
        setupBackdropDismiss();
    }

    /** Opens the modal and starts scanning. */
    public void show() {
        resetModalState();
        beginScanning();
    }

    /** Closes the modal and stops scanning. */
    public void hide() {
        removeBackgroundBlur();
        playExitAnimation(() -> {
            modalOverlay.setVisible(false);
            endScanning();
        });
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

    private void setupBackdropDismiss() {
        modalOverlay.setOnMouseClicked(event -> {
            if (event.getTarget() == modalOverlay) hide();
        });
    }

    private void resetModalState() {
        deviceListView.clear();
        loadingSpinner.setVisible(true);
        statusLabel.setText("Scanning local network for ESP32...");
        modalOverlay.setOpacity(0.0);
        modalOverlay.setVisible(true);
    }

    private void playExitAnimation(Runnable onFinished) {
        FadeTransition fade = createFadeTransition(1.0, 0.0, 50);
        fade.setOnFinished(event -> onFinished.run());
        fade.play();
        createScaleTransition(1.0, 0.88, 50).play();
    }

    private FadeTransition createFadeTransition(double from, double to, int durationMs) {
        FadeTransition transition = new FadeTransition(Duration.millis(durationMs), modalOverlay);
        transition.setFromValue(from);
        transition.setToValue(to);
        return transition;
    }

    private ScaleTransition createScaleTransition(double from, double to, int durationMs) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(durationMs), modalCard);
        transition.setFromX(from);
        transition.setFromY(from);
        transition.setToX(to);
        transition.setToY(to);
        return transition;
    }

    private void beginScanning() {
        loadingSpinner.setVisible(true);
        wifiScanner.startScanning(devices ->
                Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    deviceListView.render(devices, connectedDeviceName);
                }));
    }

    private void endScanning() { wifiScanner.stopScanning(); }

    private void refreshDeviceList() {
        deviceListView.render(wifiScanner.getDevices(), connectedDeviceName);
    }

    private void handleDeviceConnection(EspDevice device) {
        connectedDeviceName = device.name();
        if (onDeviceConnected != null) onDeviceConnected.accept(device);
        hide();
    }
}
