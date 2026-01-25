package in.hamshik;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;


public class EndGameHandler {

    @FXML private Label tagLabel, scorLabel;
    @FXML private ProgressIndicator processIndicator;
    @FXML private Button tryAgainBtn, goToMainPage;

    private MControllerVar mControllerVar;
    private QuizManager quizManager;

    public void setData( MControllerVar mControllerVar, QuizManager quizManager) throws Exception {
        this.mControllerVar = mControllerVar;
        this.quizManager = quizManager;
        showFinalResult();
    }
    
    public void showFinalResult() throws Exception{
        int percent =
                (int) (quizManager.getScore() * 100.0
                        / quizManager.getTotalQuestions());

        setProgress(0);
        scorLabel.setText(
                quizManager.getScore() + " / "
                        + quizManager.getTotalQuestions()
        );

        String setTag =
                percent < 25 ? "Poor" :
                percent < 50 ? "Average" :
                percent < 75 ? "Good" : "Excellent";

        tagLabel.setText(setTag);

        Thread progressThread = new Thread(() -> {
            for (int i = 0; i <= percent; i++) {
                int p = i;
                Platform.runLater(() -> setProgress(p));
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        progressThread.start();
    }

    public void setProgress(int percent) {
        double progress = percent / 100.0;
        processIndicator.setProgress(progress);

        if (percent < 25)
            processIndicator.setStyle("-fx-accent: red;");
        else if (percent < 50)
            processIndicator.setStyle("-fx-accent: yellow;");
        else
            processIndicator.setStyle("-fx-accent: green;");
    }




    @FXML public void tryAgian(ActionEvent e) throws IOException{
        mControllerVar.numberOfUserAttempts++;

        System.out.println("try again clicked");
        
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/main.fxml")
        );

        Parent root = loader.load();

        // Get current stage from button click
        Stage stage = (Stage) ((Node) e.getSource())
                .getScene()
                .getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML public void goToMainPage(ActionEvent e) throws IOException{
        System.out.println("go to main page clicked");
        
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/start.fxml")
        );

        Parent root = loader.load();

        
        Stage stage = (Stage) ((Node) e.getSource())
                .getScene()
                .getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}