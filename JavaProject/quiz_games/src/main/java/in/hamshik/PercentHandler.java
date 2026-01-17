package in.hamshik;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class PercentHandler {

    @FXML private Label tagLabel, scorLabel;
    @FXML private ProgressIndicator processIndicator;
    @FXML private StackPane progressPane;


    MainController controller = (new FXMLLoader(getClass().getResource("/main.fxml"))).getController();

    private QuizManager quizManager = controller.getQuizManager();


    public void showFinalResult() {
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

    public void endGame(List<Button> buttons,
                         List<Text> groupText,
                         List<Label> groupLabel,
                         ImageView resultImage) {
        progressPane.setVisible(true);
        progressPane.setManaged(true);

        buttons.forEach(b -> {
            b.setVisible(false);
            b.setManaged(false);
        });

        groupText.forEach(t -> {
            t.setVisible(false);
            t.setManaged(false);
        });

        groupLabel.forEach(l -> {
            l.setManaged(true);
            l.setVisible(true);
        });

        resultImage.setVisible(false);
        resultImage.setManaged(false);

        showFinalResult();
    }
}