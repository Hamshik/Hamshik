package in.hamshik;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML private Button choice1, choice2, choice3, choice4, nextBut, startBut, exitBut, leaderboard;
    @FXML private Text ques_text, correctOrIncorrect_text;
    @FXML private ImageView resultImage;
    @FXML private ProgressIndicator processIndicator;
    @FXML private StackPane progressPane;
    @FXML private Label tagLabel;
    @FXML private Label scorLabel;
    @FXML private TextArea welcomText;
    List<Button> buttons;
    List<Text> groupText;
    List<Label> groupLabel;

    private final Image correctImg =
            new Image(App.class.getResource("/correct.png").toExternalForm());
    private final Image wrongImg =
            new Image(App.class.getResource("/incorrect.png").toExternalForm());

    private QuizManager quizManager;
    private boolean shouldGONext = false;
    private boolean isCorrect = false;
    private String userAnswer;
    private List<QuizEntry> quizzes;
    private boolean transitionRunning = false;

    public void initialize(URL arg0, ResourceBundle arg1) {
        quizzes = QuizRepository.loadQuizzes("/quiz.json");
        Collections.shuffle(quizzes);
        quizManager = new QuizManager(quizzes);
        buttons =
            List.of(choice1, choice2, choice3, choice4, nextBut)
        ;
        groupText =
            List.of(correctOrIncorrect_text, ques_text)
        ;
        groupLabel =
            List.of(scorLabel, tagLabel)
        ;
        showQuestion();

    }

    private void showQuestion() {
        QuizEntry q = quizManager.getCurrentQuestion();

        ques_text.setText(
                quizManager.getCurrentIndex() + ". " + q.question()
        );

        for (int i = 1; i < buttons.size(); i++) {
            buttons.get(i - 1).setText((i + 1) + ". " + q.choices().get(i - 1));
        }

        resetChoiceButtons();
        resultImage.setVisible(false);
        correctOrIncorrect_text.setVisible(false);
        shouldGONext = false;
    }

    @FXML private void handleAns(ActionEvent e) {

        for (Button btn : buttons) {
            btn.getStyleClass().removeAll("activeBut", "inactiveBut");
            btn.getStyleClass().add("inactiveBut");
            btn.setDisable(false);
        }

        Button btn = (Button) e.getSource();
        userAnswer = btn.getText().substring(3);
        isCorrect = quizManager.checkAnswer(userAnswer);
        shouldGONext = true;

        btn.getStyleClass().remove("inactiveBut");
        btn.getStyleClass().add("activeBut");
    }

    @FXML private void handleNext() {
        if (!shouldGONext) return;

        if (!quizManager.hasNext()) {
            endGame();
        } else {
            goToNextQues();
        }
    }

    private void showResult(boolean isCorrect) {
        resultImage.setImage(isCorrect ? correctImg : wrongImg);
        resultImage.setVisible(true);

        correctOrIncorrect_text.setText(
                isCorrect ? "Correct" : "Incorrect"
        );
        correctOrIncorrect_text.getStyleClass()
                .removeAll("correct", "incorrect");
        correctOrIncorrect_text.getStyleClass()
                .add(isCorrect ? "correct" : "incorrect");
        correctOrIncorrect_text.setVisible(true);
    }

    private void crossFade(Image newImage) {
        FadeTransition fadeOut =
                new FadeTransition(Duration.millis(150), resultImage);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            resultImage.setImage(newImage);
            FadeTransition fadeIn =
                    new FadeTransition(Duration.millis(150), resultImage);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void resetChoiceButtons() {

        for (Button btn : buttons) {
            btn.getStyleClass().removeAll("activeBut", "inactiveBut");
            btn.getStyleClass().add("inactiveBut");
            btn.setDisable(false);
        }
    }

    private void showFinalResult() {
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

    private void goToNextQues() {
        if (transitionRunning) return;
        transitionRunning = true;

        for (Button btn : buttons) {
            btn.setDisable(true);
        }

        showResult(isCorrect);
        crossFade(isCorrect ? correctImg : wrongImg);

        PauseTransition pause =
                new PauseTransition(Duration.millis(500));
        pause.setOnFinished(event -> {
            quizManager.nextQuestion();
            showQuestion();

            for (Button btn : buttons) {
                btn.setDisable(false);
            }

            transitionRunning = false;
        });

        pause.play();
    }

    private void endGame() {
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

    @FXML private void startGame(){}

    @FXML private void exitGame(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Exit");
        alert.setHeaderText(null);
        alert.setContentText("App will exit");
        alert.showAndWait();
        Platform.exit();
    }

    @FXML private void showResultPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/leaderboard.fxml")
            );
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Leaderboard");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}