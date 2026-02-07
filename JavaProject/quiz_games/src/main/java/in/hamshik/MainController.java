package in.hamshik;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.reflect.TypeToken;

public class MainController implements Initializable{

    @FXML private Button choice1, choice2, choice3, choice4, nextBut, goBack;
    @FXML private Text ques_text, correctOrIncorrect_text;
    @FXML private ImageView resultImage;
    @FXML private ProgressIndicator processIndicator;
    @FXML private StackPane progressPane;
    @FXML private Label tagLabel;
    @FXML private Label scorLabel;
    @FXML private TextArea welcomText;
    private Image wrongImg = new Image(getClass().getResourceAsStream("/incorrect.png"));
    private Image correctImg = new Image(getClass().getResourceAsStream("/correct.png"));


    private QuizManager quizManager;
    private List<QuizEntry> quizzes;
    private List<Button> buttons;
    private MControllerVar mControllerVar;
    private UIManger uiManger;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        Stage loadingStage = UIManger.createLoadingStage();
        loadingStage.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {

                // Background work (NOT UI)
                Thread pyThread = new Thread(UIManger::writeJson);
                pyThread.setDaemon(true);
                pyThread.start();

                if (!MControllerVar.isRunning) {
                    Type quizListType = new TypeToken<List<QuizEntry>>() {}.getType();
                    quizzes = StaticUtilities.load("/quiz.json", quizListType);
                    Collections.shuffle(quizzes);
                }

                return null;
            }
        };

        loadTask.setOnSucceeded(e -> {
            // UI work after loading
            loadingStage.close();

            buttons = List.of(choice1, choice2, choice3, choice4, nextBut);
            quizManager = new QuizManager(quizzes);
            mControllerVar = new MControllerVar();
            mControllerVar.buttons = buttons;

            uiManger = new UIManger(quizManager, mControllerVar);
            showQuestion();
        });

        loadTask.setOnFailed(e -> {
            loadingStage.close();
            loadTask.getException().printStackTrace();
        });

        new Thread(loadTask).start();
    }



    private void showQuestion() {
        int currentIndex = quizManager.getCurrentIndex();
        QuizEntry q = quizManager.getCurrentQuestion(currentIndex);
        Collections.shuffle(q.choices());

        ques_text.setText((currentIndex + 1) + ". " + q.question());

        for (int i = 0; i < 4; i++) {
            buttons.get(i).setText((i + 1) + ". " + q.choices().get(i));
            buttons.get(i).setDisable(false);
        }

        uiManger.resetChoiceButtons(buttons);
        mControllerVar.shouldGONext = false;
    }

    @FXML
    private void handleAns(ActionEvent e) {
        quizManager.handleAns(e, mControllerVar,resultImage, correctOrIncorrect_text);
    }

    @FXML
    private void handleNext(ActionEvent e) throws Exception {

        if (!mControllerVar.shouldGONext) return;

        if (!quizManager.hasNext()) {

            nextBut.setText("Submit");
            if (((Button) e.getSource()) == nextBut)uiManger.runAct(() ->  uiManger.showFinalScore(e));
        } else {
            nextBut.setText("Next");
            quizManager.showResult(
                mControllerVar.isCorrect, 
                correctImg, wrongImg, resultImage, 
                correctOrIncorrect_text
            );
            resultImage.setVisible(false);
            correctOrIncorrect_text.setVisible(false);
            goToNextQues();
        }
    }

    private void goToNextQues() {
        if (isTransitionRunning()) return;
        setTransitionRunning(true);
        for (Button btn : buttons) btn.setDisable(true);
        uiManger.runAct(this::showQuestion);
    }

    public QuizManager getQuizManager() { return quizManager; }
    public void setTransitionRunning(boolean transitionRunning) { mControllerVar.transitionRunning = transitionRunning; }
    public boolean isTransitionRunning() { return mControllerVar.transitionRunning; }
    public MControllerVar getMControllerVar() { return mControllerVar; }

    @FXML
    public void goBack(ActionEvent e) {
        if (quizManager.getCurrentIndex() > 0) {
            quizManager.setIndex(quizManager.getCurrentIndex() - 1);
            showQuestion();
        }
    }
    
}
