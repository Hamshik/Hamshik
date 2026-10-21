package in.hamshik.Controller;

import in.hamshik.DNSHandler.WiFiScanner.EspDevice;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.function.Consumer;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

/** Form for connecting to an ESP32 by its manually entered network address. */
public class ManualConnectionCardCtrl {
    @FXML private TextField ipField;
    @FXML private TextField hostNameField;
    @FXML private TextField portField;
    @FXML private TextField networkTypeField;
    @FXML private TextField pathField;
    @FXML private Label connectionError;

    private Consumer<EspDevice> onConnect;
    private Runnable onDismiss;

    boolean verifyFeilds(String... fields){
        for(var field: fields)
            if(field == null || field.isEmpty()) {
                showError("Pls Fill the feilds");
                return false;
            }
        return true;
    }
    @FXML
    private void connect() {
        String host = ipField.getText();
        String name = hostNameField.getText();
        String type = networkTypeField.getText();
        String path = pathField.getText();

        if(!verifyFeilds(host, name, type, path)) return;

        final int port;

        try {
            port = Integer.parseInt(portField.getText().strip());
        } catch (NumberFormatException e) {
            showError("Enter a valid port number (1–65535).");
            return;
        }
        if (!isIpAddress(host)) {
            showError("Enter a valid IPv4 or IPv6 address.");
            return;
        }
        if (port < 1 || port > 65535) {
            showError("Enter a valid port number (1–65535).");
            return;
        }
        if (path.isEmpty()) path = "/ws";
        if (!path.startsWith("/")) path = "/" + path;
        connectionError.setText(" ");

        if (onConnect != null) {
            onConnect.accept(new EspDevice(name, host, port, type, path));
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
        connectionError.setText(" ");
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
        } catch (Exception e) {
            return false;
        }
    }

    private void showError(String message) {
        connectionError.setText(message);
    }

    public void setOnConnect(Consumer<EspDevice> onConnect) { this.onConnect = onConnect; }
    public void setOnDismiss(Runnable onDismiss) { this.onDismiss = onDismiss; }
}
