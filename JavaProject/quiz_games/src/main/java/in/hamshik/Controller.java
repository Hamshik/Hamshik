package in.hamshik;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

public class Controller {

    @FXML private Button choice1, choice2, choice3, choice4, nextBut;
    @FXML private Text ques_text, correctOrIncorrect_text;
    @FXML private ImageView resultImage;
    @FXML private ProgressIndicator processIndicator;
    @FXML private StackPane progressPane;
    @FXML private Label tagLabel;
    @FXML private Label scorLabel;

    private final Image correctImg = new Image(App.class.getResource("/correct.png").toExternalForm());
    private final Image wrongImg = new Image(App.class.getResource("/incorrect.png").toExternalForm());

    private QuizManager quizManager;
    private boolean shouldGONext = false;
    private boolean isCorrect = false;

    public void initialize() {
        progressPane.setVisible(false);
        progressPane.setManaged(false);
        scorLabel.setManaged(false);
        tagLabel.setManaged(false);
        processIndicator.setScaleX(7.5);
        processIndicator.setScaleY(7.5);
        List<QuizQuestion> quizzes = QuizRepository.loadQuizzes("/quiz.json");
        quizManager = new QuizManager(quizzes);
        showQuestion();
    }

    private void showQuestion() {
        QuizQuestion q = quizManager.getCurrentQuestion();
        ques_text.setText((quizManager.getCurrentIndex() + 1) + ". " + q.getQuestion());

        choice1.setText(q.getChoices().get(0));
        choice2.setText(q.getChoices().get(1));
        choice3.setText(q.getChoices().get(2));
        choice4.setText(q.getChoices().get(3));

        resetChoiceButtons();
        resultImage.setVisible(false);
        correctOrIncorrect_text.setVisible(false);
        shouldGONext = false;
    }

    @FXML private void handleAns(ActionEvent e) {
        List<Button> groupBut = List.of(choice1, choice2, choice3, choice4);

        for (Button b : groupBut) {
            b.getStyleClass().removeAll("activeBut", "inactiveBut");
            b.getStyleClass().add("inactiveBut");
            b.setDisable(false);
        }

        Button btn = (Button) e.getSource();
        int selectedIndex = Integer.parseInt(btn.getUserData().toString());

        isCorrect = quizManager.checkAnswer(selectedIndex);
        shouldGONext = true;

        btn.getStyleClass().remove("inactiveBut");
        btn.getStyleClass().add("activeBut");

        showResult(isCorrect);
        crossFade(isCorrect ? correctImg : wrongImg);
    }

    @FXML private void handleNext() {
        List<Button> groupBut = List.of(choice1, choice2, choice3, choice4, nextBut);
        List<Text> groupText = List.of(correctOrIncorrect_text, ques_text);
        List<Label> groupLabel = List.of(scorLabel, tagLabel);
        if (!shouldGONext) return;

        if (quizManager.hasNext()) {
            quizManager.nextQuestion();
            showQuestion();
        } else {
            progressPane.setVisible(true);
            progressPane.setManaged(true);

            for (Button b : groupBut) {
                b.setVisible(false);
                b.setManaged(false);
            }
            for (Text t : groupText) {
                t.setVisible(false);
                t.setManaged(false);
            }
            for (Label l : groupLabel) {
                l.setManaged(true);
                l.setVisible(true);
            }
            resultImage.setVisible(false);
            resultImage.setManaged(false);
            showFinalResult();
        }
    }

    private void showResult(boolean isCorrect) {
        resultImage.setImage(isCorrect ? correctImg : wrongImg);
        resultImage.setVisible(true);
        correctOrIncorrect_text.setText(isCorrect ? "Correct" : "Incorrect");
        correctOrIncorrect_text.getStyleClass().removeAll("correct", "incorrect");
        correctOrIncorrect_text.getStyleClass().add(isCorrect ? "correct" : "incorrect");
        correctOrIncorrect_text.setVisible(true);
    }

    private void crossFade(Image newImage) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), resultImage);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            resultImage.setImage(newImage);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), resultImage);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void resetChoiceButtons() {
        List<Button> groupBut = List.of(choice1, choice2, choice3, choice4);
        for (Button b : groupBut) {
            b.getStyleClass().removeAll("activeBut", "inactiveBut");
            b.getStyleClass().add("inactiveBut");
            b.setDisable(false);
        }
    }

    private void showFinalResult() {
        int percent = (int) (quizManager.getScore() * 100.0 / quizManager.getTotalQuestions());
        setProgress(0);
        scorLabel.setText(quizManager.getScore() + " / " + quizManager.getTotalQuestions());

        String setTag = percent < 25 ? "Poor" : 
            percent < 50 ? "Average" : 
            percent < 75 ? "Good" : "Excellent"
        ;
        
        tagLabel.setText(setTag);

        Thread progressThread = new Thread(() -> {
            for (int i = 0; i <= percent; i++) {
                int p = i;
                Platform.runLater(() -> setProgress(p));
                try { Thread.sleep(20); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
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
}