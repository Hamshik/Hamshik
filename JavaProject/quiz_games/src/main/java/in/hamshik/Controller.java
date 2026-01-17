package in.hamshik;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller {

    @FXML private Button startBut, exitBut, quizPerformBut;
    @FXML private TextArea welcomText;

    @FXML public void startQuiz(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/main.fxml")
        );

        Parent root = loader.load();

        // Get current stage from button click
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML public void exitQuiz() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Press OK to exit or Cancel to stay.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
            }
        });
    }

    @FXML public void performQuiz(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/quizperform.fxml")
        );
        Parent root = loader.load();

        Stage owner = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        Stage popupStage = new Stage();
        popupStage.setTitle("Perform Quiz");
        popupStage.initOwner(owner);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setScene(new Scene(root));
        popupStage.showAndWait();
    }

}