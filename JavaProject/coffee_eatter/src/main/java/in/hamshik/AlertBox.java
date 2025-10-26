package in.hamshik;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

/**
 * Handles application-level confirmation dialogs (exit, reset, etc.)
 */
public class AlertBox {

    public static void showExitConfirmation(WindowEvent event, Runnable onConfirm) {
        event.consume(); // prevent default close
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you really want to exit?",
                ButtonType.YES,
                ButtonType.NO);

        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Exit Game?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                onConfirm.run();       // stop threads
                Platform.exit();       // closes JavaFX
            }
            else return;
            // NO -> do nothing
        });
    }
}
