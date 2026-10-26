package in.hamshik.DNSHandler;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import in.hamshik.DNSHandler.WiFiScanner.EspDevice;
import in.hamshik.Helpers.CloudRequest;
import in.hamshik.WifiHandler.WifiSender;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Esp32WebSocketClient extends WebSocketClient {

    public interface DeviceListener {
        void onDisconnectedCallback();
        void onConnectingState();
        void onFailedConnection();
        default void onConnected() {}
        default void onVariablesReceived(JsonObject response) {}
        default void onVariableUpdated(String varName, JsonElement value) {}
    }

    private static final AtomicReference<Esp32WebSocketClient> ACTIVE_CLIENT = new AtomicReference<>();
    private static final long CONNECT_TIMEOUT_MILLIS = 5_000;
    private static final ScheduledExecutorService CONNECT_TIMEOUTS =
            Executors.newSingleThreadScheduledExecutor(task -> {
                Thread thread = new Thread(task, "esp32-connect-timeout");
                thread.setDaemon(true);
                return thread;
            });

    private final DeviceListener onDeviceListener;
    private final AtomicBoolean completionNotified = new AtomicBoolean();
    private final AtomicBoolean connected = new AtomicBoolean();
    private final EspDevice deviceConnectedOn;
    private volatile ScheduledFuture<?> connectionTimeout;

    public Esp32WebSocketClient(URI serverUri, DeviceListener onDeviceListener, EspDevice connectedOn) {
        super(serverUri, new Draft_6455(), null, (int) CONNECT_TIMEOUT_MILLIS);
        this.onDeviceListener = onDeviceListener;
        this.deviceConnectedOn = connectedOn;
        setDaemon(true);
        setConnectionLostTimeout((int) CONNECT_TIMEOUT_MILLIS);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        cancelConnectionTimeout();
        if (ACTIVE_CLIENT.get() != this) {
            close();
            return;
        }
        connected.set(true);
        if (ACTIVE_CLIENT.get() != this) {
            connected.set(false);
            close();
            return;
        }
        System.out.println("Connected to ESP32 WebSocket!");
        notifyListener(DeviceListener::onConnected);
        JsonObject request = CloudRequest.getAllVar();
        CompletableFuture
                .supplyAsync(() -> WifiSender.sendDataToESP32(deviceConnectedOn, request.toString()))
                .thenAccept(response -> {
                    if (response != null && ACTIVE_CLIENT.get() == this && connected.get())
                        notifyListener(listener -> listener.onVariablesReceived(response));
                });
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Recieved: " + message);
        try {
            JsonElement root = JsonParser.parseString(message);
            if (root.isJsonObject()) {
                JsonObject json = root.getAsJsonObject();
                if (json.has("VarName") && json.has("Value")) {
                    String varName = json.get("VarName").getAsString();
                    JsonElement val = json.get("Value");
                    notifyListener(listener -> listener.onVariableUpdated(varName, val));
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing WebSocket message: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        cancelConnectionTimeout();
        System.out.println("Connection closed: " + reason);
        if (ACTIVE_CLIENT.compareAndSet(this, null)) notifyCompletion();
    }

    @Override
    public void onError(Exception ex) {
        cancelConnectionTimeout();
        System.err.println("WebSocket Error caught: " + ex.getMessage());
        if (ACTIVE_CLIENT.compareAndSet(this, null)) {
            notifyCompletion();
            close();
        }
    }

    private void startConnectionTimeout() {
        connectionTimeout = CONNECT_TIMEOUTS.schedule(() -> {
            if (!connected.get() && ACTIVE_CLIENT.compareAndSet(this, null)) {
                System.err.println("WebSocket connection timed out after "
                        + CONNECT_TIMEOUT_MILLIS + " ms: " + getURI());
                notifyCompletion();
                close();
            }
        }, CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    private void cancelConnectionTimeout() {
        ScheduledFuture<?> timeout = connectionTimeout;
        if (timeout != null) timeout.cancel(false);
    }

    private void notifyCompletion() {
        cancelConnectionTimeout();
        if (!completionNotified.compareAndSet(false, true)) return;
        if (connected.get()) {
            notifyListener(DeviceListener::onDisconnectedCallback);
        } else {
            notifyListener(DeviceListener::onFailedConnection);
        }
    }

    private void notifyListener(Consumer<DeviceListener> callback) {
        if (onDeviceListener != null) {
            javafx.application.Platform.runLater(() -> callback.accept(onDeviceListener));
        }
    }

    public static void connect(EspDevice device) {
        connect(device, null);
    }

    public static void connect(EspDevice device, DeviceListener onDeviceListener) {
        Esp32WebSocketClient wsClient = null;
        try {
            URI wsUri = new URI("ws", null, device.host(), device.port(), device.path(), null, null);
            wsClient = new Esp32WebSocketClient(wsUri, onDeviceListener, device);
            Esp32WebSocketClient previousClient = ACTIVE_CLIENT.getAndSet(wsClient);
            if (previousClient != null) previousClient.close();

            wsClient.startConnectionTimeout();
            wsClient.notifyListener(DeviceListener::onConnectingState);
            wsClient.connect();

        } catch (Exception e) {
            System.err.println("Failed to initialize WebSocket: " + e.getMessage());
            if (wsClient != null && ACTIVE_CLIENT.compareAndSet(wsClient, null)) {
                wsClient.close();
                wsClient.notifyCompletion();
            } else if (wsClient == null && onDeviceListener != null) {
                javafx.application.Platform.runLater(() -> onDeviceListener.onFailedConnection());
            }
        }
    }

    public static void disconnect() {
        Esp32WebSocketClient client = ACTIVE_CLIENT.getAndSet(null);
        if (client != null) client.close();
    }
}
