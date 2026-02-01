package in.hamshik;

import java.util.List;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ShowCurrentUsrActivity {
    boolean isTimmerOver;
    int timer;
    QuizManager quizManager;
    List<Button> buttons;
    MControllerVar mControllerVar;
    Text timerText;
    MainController mainController;
    @FXML Button goBckBut, submitBut, unansweredBut;
    @FXML Text timerTextLabel;
    @FXML ProgressIndicator processIndicator;

    public void setData(
        boolean isTimmerOver,
        int timer,
        QuizManager quizManager,
        List<Button> buttons,
        MControllerVar mControllerVar,
        MainController mainController
    ) {
        this.isTimmerOver = isTimmerOver;
        this.timer = timer;
        this.quizManager = quizManager;
        this.buttons = buttons;
        this.mControllerVar = mControllerVar;
        this.mainController = mainController;
    }

    @FXML public void showPartialResult(ActionEvent e){
        Thread workerThread = new Thread(() ->{
            while (!isTimmerOver) {
                Platform.runLater(() ->
                    timerTextLabel.setText(timer + " This much Time is left for you")
                );

            }
            for (int i = 0; i <= timer; i++) {
                int p = i;
                Platform.runLater(() -> {
                    double progress = (int)((p / (double) timer) * 100);
                    processIndicator.setProgress(progress);
                    if (progress < 25)
                        processIndicator.setStyle("-fx-accent: red;");
                    else if (progress < 50)
                        processIndicator.setStyle("-fx-accent: yellow;");
                    else
                        processIndicator.setStyle("-fx-accent: green;");
                });
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

            Platform.runLater(() -> mainController.showFinalScore(e));
        });
        workerThread.setDaemon(true);
        workerThread.start();
    }

    @FXML public void goBackToQuiz(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
