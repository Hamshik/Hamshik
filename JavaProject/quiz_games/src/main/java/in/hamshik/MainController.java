package in.hamshik;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
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


    private QuizManager quizManager;
    private List<QuizEntry> quizzes;
    private List<Button> buttons;
    private MControllerVar mControllerVar;
    private List<UserAnsEntry> userAnsLs = new ArrayList<>();

    private int timer;
    private boolean isTimmerOver = false;

    private Thread timmeThread = new Thread(() -> {
        try {
            for (timer = 60; timer >= 0; timer--) {
                Thread.sleep(1000);
            }
            isTimmerOver = true;
        } catch (InterruptedException e) {
            // ignored
        }
    });

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Type quizListType = new TypeToken<List<QuizEntry>>() {}.getType();

        try {
            quizzes = StaticUtilities.load("/quiz.json", quizListType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.shuffle(quizzes);

        buttons = List.of(choice1, choice2, choice3, choice4, nextBut);
        quizManager = new QuizManager(quizzes);
        mControllerVar = new MControllerVar();
        mControllerVar.buttons = buttons;

        showQuestion();
        timmeThread.setDaemon(true);
        timmeThread.start();
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

        UIManger.resetChoiceButtons(buttons);
        mControllerVar.shouldGONext = false;
    }

    @FXML
    private void handleAns(ActionEvent e) {
        quizManager.handleAns(e, mControllerVar.userAnswer, mControllerVar, userAnsLs, true);
    }

    @FXML
    private void handleNext(ActionEvent e) throws Exception {

        if (!mControllerVar.shouldGONext) return;

        if (isTimmerOver) {
            showFinalScore(e);
            return;
        }

        if (!quizManager.hasNext()) {
            nextBut.setText("Submit");

            if (((Button) e.getSource()) == nextBut) {

                if (isTimmerOver) {
                    UIManger.runNextQues(
                        quizManager,
                        buttons,
                        () -> showFinalScore(e),
                        mControllerVar
                    );
                } else {

                    Thread t = new Thread(() -> {
                        while (!isTimmerOver) {
                            Platform.runLater(() ->
                                ques_text.setText(timer + " This much Time is left for you")
                            );
                        }

                        Platform.runLater(() ->
                            UIManger.runNextQues(
                                quizManager,
                                buttons,
                                () -> showFinalScore(e),
                                mControllerVar
                            )
                        );

                    });

                    t.setDaemon(true);
                    t.start();
                }
            }
        } else {
            nextBut.setText("Next");
            goToNextQues();
        }
    }

    private void goToNextQues() {
        if (isTransitionRunning()) return;
        setTransitionRunning(true);
        for (Button btn : buttons) btn.setDisable(true);
        UIManger.runNextQues(quizManager, buttons, this::showQuestion, mControllerVar);
    }

    public QuizManager getQuizManager() { return quizManager; }
    public void setTransitionRunning(boolean transitionRunning) { mControllerVar.transitionRunning = transitionRunning; }
    public boolean isTransitionRunning() { return mControllerVar.transitionRunning; }
    public MControllerVar getMControllerVar() { return mControllerVar; }

    public void showFinalScore(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/percentHandler.fxml"));
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

    @FXML
    public void goBack(ActionEvent e) {
        if (quizManager.getCurrentIndex() > 0) {
            quizManager.setIndex(quizManager.getCurrentIndex() - 1);
            showQuestion();
        }
    }
}
