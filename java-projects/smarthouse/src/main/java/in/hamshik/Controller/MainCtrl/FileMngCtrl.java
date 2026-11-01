package in.hamshik.Controller.MainCtrl;

import in.hamshik.App;
import in.hamshik.DNSHandler.ESP32FlashUploader;
import in.hamshik.DNSHandler.WiFiScanner.EspDevice;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.nio.file.Files;

public class FileMngCtrl extends Controller {

    @FXML private TextField firmwarePathField;
    @FXML private Button uploadButton;
    @FXML private Label statusLabel;

    @FXML
    private void browseFirmwareFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose ESP32 firmware");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Firmware files (*.bin)", "*.bin"));

        Window window = firmwarePathField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            firmwarePathField.setText(selectedFile.getAbsolutePath());
            statusLabel.setText("Firmware file selected.");
        }
    }

    @FXML
    private void uploadFirmware() {
        EspDevice device = App.deviceMngCtrl.activeDevice;
        if (device == null) {
            statusLabel.setText("Connect to an ESP32 before uploading firmware.");
            return;
        }
        String firmwarePath = firmwarePathField.getText().trim();

        if (firmwarePath.isEmpty()) {
            statusLabel.setText("Choose a firmware .bin file first.");
            return;
        }

        File firmwareFile = new File(firmwarePath);
        if (!Files.isRegularFile(firmwareFile.toPath()) || !Files.isReadable(firmwareFile.toPath())) {
            statusLabel.setText("The selected firmware file cannot be read. Choose it again.");
            return;
        }

        Task<String> uploadTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return ESP32FlashUploader.uploadBin(device, firmwareFile.getAbsolutePath());
            }
        };

        uploadButton.setDisable(true);
        statusLabel.setText("Uploading firmware...");
        uploadTask.setOnSucceeded(event -> {
            statusLabel.setText("Firmware uploaded successfully: " + uploadTask.getValue() + ".");
            uploadButton.setDisable(false);
        });
        uploadTask.setOnFailed(event -> {
            Throwable error = uploadTask.getException();
            String reason = error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
            statusLabel.setText("Upload failed: " + reason);
            uploadButton.setDisable(false);
        });

        Thread uploadThread = new Thread(uploadTask, "esp32-firmware-upload");
        uploadThread.setDaemon(true);
        uploadThread.start();
    }
}
