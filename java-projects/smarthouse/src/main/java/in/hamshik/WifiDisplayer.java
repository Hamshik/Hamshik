package in.hamshik;

import in.hamshik.WiFiScanner.EspDevice;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

/**
 * Manages the presentation, animations, background blur, and device card UI
 * for the Wi-Fi / ESP32 network scanning modal.
 */
public class WifiDisplayer {

    private final Node backgroundNode;
    private final StackPane modalOverlay;
    private final VBox modalCard;
    private final ProgressIndicator loadingSpinner;
    private final Label statusLabel;
    private final VBox deviceListContainer;
    private final Consumer<EspDevice> onDeviceConnected;
    private final Runnable onDeviceDisconnected;

    private final WiFiScanner WiFiScanner = new WiFiScanner();
    private final GaussianBlur blurEffect = new GaussianBlur(15);

    private String connectedDeviceName = null;

    public WifiDisplayer(
            Node backgroundNode,
            StackPane modalOverlay,
            VBox modalCard,
            ProgressIndicator loadingSpinner,
            Label statusLabel,
            VBox deviceListContainer,
            Consumer<EspDevice> onDeviceConnected) {

        this(backgroundNode, modalOverlay, modalCard, loadingSpinner, statusLabel,
                deviceListContainer, onDeviceConnected, null);
    }

    public WifiDisplayer(
            Node backgroundNode,
            StackPane modalOverlay,
            VBox modalCard,
            ProgressIndicator loadingSpinner,
            Label statusLabel,
            VBox deviceListContainer,
            Consumer<EspDevice> onDeviceConnected,
            Runnable onDeviceDisconnected) {

        this.backgroundNode = backgroundNode;
        this.modalOverlay = modalOverlay;
        this.modalCard = modalCard;
        this.loadingSpinner = loadingSpinner;
        this.statusLabel = statusLabel;
        this.deviceListContainer = deviceListContainer;
        this.onDeviceConnected = onDeviceConnected;
        this.onDeviceDisconnected = onDeviceDisconnected;

        setupBackdropDismiss();
    }

    // ─── 1. Public Controls ─────────────────────────────────────

    /**
     * Opens the modal dialog with entrance animation and starts scanning.
     */
    public void show() {
        applyBackgroundBlur();
        resetModalState();
        playEntranceAnimation();
        beginScanning();
    }

    /**
     * Closes the modal dialog with exit animation and stops scanning.
     */
    public void hide() {
        removeBackgroundBlur();
        playExitAnimation(() -> {
            modalOverlay.setVisible(false);
            endScanning();
        });
    }

    /**
     * Disconnects the currently active device and updates UI state.
     */
    public void disconnect() {
        if (this.connectedDeviceName == null) return;

        this.connectedDeviceName = null;
        notifyDisconnection();
        refreshDeviceCards();
    }

    public boolean isConnected() {
        return connectedDeviceName != null;
    }

    public String getConnectedDeviceName() {
        return connectedDeviceName;
    }

    public void setConnectedDevice(String name) {
        this.connectedDeviceName = name;
        refreshDeviceCards();
    }

    // ─── 2. Blur Management ─────────────────────────────────────

    private void applyBackgroundBlur() {
        if (backgroundNode != null) {
            backgroundNode.setEffect(blurEffect);
        }
    }

    private void removeBackgroundBlur() {
        if (backgroundNode != null) {
            backgroundNode.setEffect(null);
        }
    }

    // ─── 3. State & Animation ───────────────────────────────────

    private void setupBackdropDismiss() {
        modalOverlay.setOnMouseClicked(event -> {
            if (event.getTarget() == modalOverlay) {
                hide();
            }
        });
    }

    private void resetModalState() {
        deviceListContainer.getChildren().clear();
        loadingSpinner.setVisible(true);
        statusLabel.setText("Scanning local network for ESP32...");
        modalOverlay.setOpacity(0.0);
        modalOverlay.setVisible(true);
    }

    private void playEntranceAnimation() {
        FadeTransition fade = createFadeTransition(0.0, 1.0, 220);
        ScaleTransition scale = createScaleTransition(0.85, 1.0, 220);
        fade.play();
        scale.play();
    }

    private void playExitAnimation(Runnable onFinished) {
        FadeTransition fade = createFadeTransition(1.0, 0.0, 160);
        ScaleTransition scale = createScaleTransition(1.0, 0.88, 160);

        fade.setOnFinished(event -> {
            if (onFinished != null) onFinished.run();
        });

        fade.play();
        scale.play();
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

    // ─── 4. Scanning Coordination ───────────────────────────────

    private void beginScanning() {
        WiFiScanner.startScanning(devices -> {
            Platform.runLater(() -> renderDeviceList(devices));
        });
    }

    private void endScanning() {
        WiFiScanner.stopScanning();
    }

    // ─── 5. Device List & Card Rendering ────────────────────────

    private void renderDeviceList(List<EspDevice> devices) {
        deviceListContainer.getChildren().clear();
        updateStatusHeader(devices.size());

        for (EspDevice device : devices) {
            boolean isDeviceConnected = device.name().equals(connectedDeviceName);
            HBox deviceCard = createDeviceCard(device, isDeviceConnected);
            deviceListContainer.getChildren().add(deviceCard);
        }
    }

    private void refreshDeviceCards() {
        renderDeviceList(WiFiScanner.getDevices());
    }

    private void updateStatusHeader(int deviceCount) {
        if (deviceCount == 0) {
            statusLabel.setText("Scanning... No devices found yet.");
        } else {
            statusLabel.setText("Found " + deviceCount + " device(s):");
        }
    }

    private HBox createDeviceCard(EspDevice device, boolean isConnected) {
        HBox card = new HBox(10);
        card.getStyleClass().add("device-card");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox infoSection = createDeviceInfoSection(device);
        Button actionButton = createActionButton(device, isConnected);

        card.getChildren().addAll(infoSection, actionButton);
        return card;
    }

    private VBox createDeviceInfoSection(EspDevice device) {
        VBox container = new VBox(2);
        HBox.setHgrow(container, Priority.ALWAYS);

        Label nameLabel = new Label(device.name());
        nameLabel.getStyleClass().add("device-name");

        Label hostLabel = new Label(device.host() + ":" + device.port() + " • " + device.type());
        hostLabel.getStyleClass().add("device-host");

        container.getChildren().addAll(nameLabel, hostLabel);
        return container;
    }

    private Button createActionButton(EspDevice device, boolean isConnected) {
        return isConnected ? createDisconnectButton(device) : createConnectButton(device);
    }

    private Button createConnectButton(EspDevice device) {
        Button button = new Button("Connect");
        button.getStyleClass().add("connect-btn");
        button.setOnAction(event -> handleDeviceConnection(device));
        return button;
    }

    private Button createDisconnectButton(EspDevice device) {
        Button button = new Button("Disconnect");
        button.getStyleClass().add("disconnect-btn");
        button.setOnAction(event -> handleDeviceDisconnection(device));
        return button;
    }

    // ─── 6. Connection Handlers ─────────────────────────────────

    private void handleDeviceConnection(EspDevice device) {
        this.connectedDeviceName = device.name();
        if (onDeviceConnected != null) {
            onDeviceConnected.accept(device);
        }
        hide();
    }

    private void handleDeviceDisconnection(EspDevice device) {
        disconnect();
    }

    private void notifyDisconnection() {
        if (onDeviceDisconnected != null) {
            onDeviceDisconnected.run();
        }
    }
}
