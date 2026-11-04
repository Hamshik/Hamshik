package in.hamshik.Controller.CardCtrl;

import in.hamshik.DNSHandler.WiFiScanner.EspDevice;
import in.hamshik.Helpers.FXMLPipeline;
import in.hamshik.App;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

/** Form for connecting to an ESP32 by its manually entered network address. */
public class WifiCardCtrl {
    @FXML private TextField ipField;
    @FXML private TextField hostNameField;
    @FXML private TextField portField;
    @FXML private TextField networkTypeField;
    @FXML private TextField pathField;
    @FXML private TextField timeoutField;
    @FXML private Label connectionError;

    private Consumer<EspDevice> onConnect;
    private Runnable onDismiss;

    @FXML
    private void connect() {
        clearErrors();
        String host = ipField.getText().trim();
        String name = hostNameField.getText().trim();
        String type = networkTypeField.getText().trim();
        String path = pathField.getText().trim();
        String portText = portField.getText().trim();
        String timeoutText = timeoutField.getText().trim();

        if (host.isEmpty()) {
            showError("Enter the ESP32 IP address.", ipField);
            return;
        }
        if (!isIpAddress(host)) {
            showError("Enter a valid IPv4 or IPv6 address.", ipField);
            return;
        }
        if (type.isEmpty()) {
            showError("Enter the network type.", networkTypeField);
            return;
        }

        final int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            showError("Port must be a whole number from 1 to 65535.", portField);
            return;
        }
        if (port < 1 || port > 65535) {
            showError("Port must be a whole number from 1 to 65535.", portField);
            return;
        }

        final int timeoutSeconds;
        try {
            timeoutSeconds = Integer.parseInt(timeoutText);
        } catch (NumberFormatException ex) {
            showError("Timeout must be a whole number from 1 to 300 seconds.", timeoutField);
            return;
        }
        if (timeoutSeconds < 1 || timeoutSeconds > 300) {
            showError("Timeout must be a whole number from 1 to 300 seconds.", timeoutField);
            return;
        }

        if (path.isEmpty()) path = "/ws";
        if (!path.startsWith("/")) path = "/" + path;

        if (onConnect != null) {
            onConnect.accept(new EspDevice(name.isEmpty() ? host : name, host, port, type, path,
                    timeoutSeconds));
        }
        if (onDismiss != null) onDismiss.run();
    }

    @FXML
    private void cancel() {
        ipField.clear();
        hostNameField.clear();
        portField.setText("80");
        networkTypeField.setText("subnet");
        pathField.setText("/ws");
        timeoutField.setText("5");
        clearErrors();
        if (onDismiss != null) onDismiss.run();
    }

    @FXML void onKeyPressed(KeyEvent keycode){
        switch(keycode.getCode()){
            case KeyCode.ENTER -> connect();
            case KeyCode.ESCAPE -> cancel();
            default -> {}
        }
    }

    private boolean isIpAddress(String host) {
        if (host.matches("[0-9.]+")) {
            String[] parts = host.split("\\.", -1);
            if (parts.length != 4) return false;
            for (String part : parts) {
                if (part.isEmpty() || part.length() > 3) return false;
                try {
                    if (Integer.parseInt(part) > 255) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        if (!host.contains(":")) return false;
        try {
            return InetAddress.getByName(host) instanceof Inet6Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private void showError(String message, TextField field) {
        FXMLPipeline.showError(message, true, connectionError, field);
    }

    private void clearErrors() {
        FXMLPipeline.showError(null, false, connectionError, ipField, hostNameField,
                portField, networkTypeField, pathField, timeoutField);
    }

    public void setOnConnect(Consumer<EspDevice> onConnect) { this.onConnect = onConnect; }
    public void setOnDismiss(Runnable onDismiss) { this.onDismiss = onDismiss; }
}
