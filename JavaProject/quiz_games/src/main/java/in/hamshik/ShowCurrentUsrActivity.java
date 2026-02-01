package in.hamshik;

import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

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
    boolean isTimeOver;


    public void setData(
        int timer,
        QuizManager quizManager,
        List<Button> buttons,
        MControllerVar mControllerVar,
        MainController mainController
    ) {
        this.timer = timer;
        this.quizManager = quizManager;
        this.buttons = buttons;
        this.mControllerVar = mControllerVar;
        this.mainController = mainController;
    }

    @FXML public void showPartialResult(ActionEvent e){
        Thread workerThread = new Thread(() ->{
            for (int i = timer; i >= 0; i--) {
                int p = i;
                Platform.runLater(() -> {
                    timerTextLabel.setText("Time Elapsed: " + p + "s");
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
                    Thread.sleep(1000);
                    if((Button)e.getSource() == submitBut) break;

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            mainController.showFinalScore(e);
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

    public void startTimer() {
    int totalTime = mainController.getQuizManager().getTotalQuestions() * 20;

    Timeline timeline = new Timeline(
        new KeyFrame(Duration.seconds(1), e -> {
            timerTextLabel.setText(
                "Time Remaining: " + timer + "s"
            );

            double progress = timer / (double) totalTime;
            processIndicator.setProgress(progress);

            if (progress > 0.5)
                processIndicator.setStyle("-fx-accent: green;");
            else if (progress > 0.25)
                processIndicator.setStyle("-fx-accent: yellow;");
            else
                processIndicator.setStyle("-fx-accent: red;");

            timer--;
            isTimeOver = (timer < 0);
        })
    );

    timeline.setCycleCount(totalTime - timer);
    timeline.play();
    if(isTimeOver) timeline.stop(); 
}

}
