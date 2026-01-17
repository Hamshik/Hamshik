package in.hamshik;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable{

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
    private boolean isOver = false;

    private final Image correctImg =
            new Image(App.class.getResource("/correct.png").toExternalForm());
    private final Image wrongImg =
            new Image(App.class.getResource("/incorrect.png").toExternalForm());

    private QuizManager quizManager;
    private volatile boolean shouldGONext = false;
    private volatile boolean isCorrect = false;
    private String userAnswer;
    private List<QuizEntry> quizzes;
    private boolean transitionRunning = false;

    Controller controller = (new FXMLLoader(getClass().getResource("/start.fxml"))).getController();

    public void initialize(URL arg0, ResourceBundle arg1) {
        quizzes = Reader.loadQuiz("/quiz.json");
        Collections.shuffle(quizzes);

        buttons = List.of(choice1, choice2, choice3, choice4, nextBut);
        groupText = List.of(correctOrIncorrect_text, ques_text);
        groupLabel = List.of(scorLabel, tagLabel);

        quizManager = new QuizManager(quizzes);
        showQuestion();
    }

    private void showQuestion() {
        QuizEntry q = quizManager.getCurrentQuestion();

        ques_text.setText(
                quizManager.getCurrentIndex() + ". " + q.question()
        );

        for (int i = 0; i < buttons.size() - 1; i++) {
            buttons.get(i).setText((i + 1) + ". " + q.choices().get(i));
        }

        UIManger.resetChoiceButtons(buttons);
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
        isCorrect = (quizManager.checkAnswer(userAnswer));
        shouldGONext = (true);

        btn.getStyleClass().remove("inactiveBut");
        btn.getStyleClass().add("activeBut");
    }

    @FXML private void handleNext() {
        if (!shouldGONext) return;
        if (!quizManager.hasNext()) isOver = true;
        else goToNextQues();
    }
    
    private void goToNextQues() {
        if (transitionRunning) return;
        transitionRunning = true;
        for (Button btn : buttons) btn.setDisable(true);
        quizManager.showResult(isCorrect, correctImg, wrongImg, resultImage, correctOrIncorrect_text);
        UIManger.crossFade(isCorrect ? correctImg : wrongImg, quizManager, resultImage, buttons, this::showQuestion);
    }
    public QuizManager getQuizManager() {return quizManager;}

    public void setIsCorrect(boolean isCorrect) {this.isCorrect = isCorrect;}
    public void setShouldGONext(boolean shouldGONext) {this.shouldGONext = shouldGONext;}
}