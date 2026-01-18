package in.hamshik;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML private Button choice1, choice2, choice3, choice4, nextBut, goBack;
    @FXML private Text ques_text, correctOrIncorrect_text;
    @FXML private ImageView resultImage;
    @FXML private ProgressIndicator processIndicator;
    @FXML private StackPane progressPane;
    @FXML private Label tagLabel;
    @FXML private Label scorLabel;
    @FXML private TextArea welcomText;

    private final Image correctImg =
            new Image(App.class.getResource("/correct.png").toExternalForm());
    private final Image wrongImg =
            new Image(App.class.getResource("/incorrect.png").toExternalForm());

    private QuizManager quizManager;
    private List<QuizEntry> quizzes;
    private List<Button> buttons;
    private MControllerVar mControllerVar;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        quizzes = Reader.loadQuiz("/quiz.json");
        Collections.shuffle(quizzes);
        buttons = List.of(choice1, choice2, choice3, choice4, nextBut);
        quizManager = new QuizManager(quizzes);
        mControllerVar = new MControllerVar();
        mControllerVar.buttons = buttons;
        showQuestion();
    }

    private void showQuestion() {
        int currentIndex = quizManager.getCurrentIndex();
        QuizEntry q = quizManager.getCurrentQuestion(currentIndex);

        ques_text.setText(
            (currentIndex + 1) + ". " + q.question()
        );

        for (int i = 0; i < 4; i++) {
            buttons.get(i).setText((i + 1) + ". " + q.choices().get(i));
            buttons.get(i).setDisable(false);
        }

        UIManger.resetChoiceButtons(buttons);
        resultImage.setVisible(false);
        correctOrIncorrect_text.setVisible(false);
        mControllerVar.shouldGONext = false;
    }


    @FXML private void handleAns(ActionEvent e) {quizManager.handleAns(e, mControllerVar.userAnswer, mControllerVar, quizManager.getCurrentIndex());}

    @FXML private void handleNext(ActionEvent e) throws Exception {
        if (!mControllerVar.shouldGONext) return;
        if (!quizManager.hasNext()){
            nextBut.setText("Submit Quiz Game");
            if(((Button)e.getSource()) == nextBut)
                UIManger.crossFade(mControllerVar.isCorrect ? correctImg : wrongImg, quizManager, resultImage, buttons, () -> {
                    try {showFinalScore(e);}
                    catch (Exception e1) {e1.printStackTrace();}}, 
                mControllerVar);
        }
        else {
            nextBut.setText("Next");
            goToNextQues();
        }
        
    }
    
    private void goToNextQues() {
        if (isTransitionRunning()) return;
        setTransitionRunning(true);
        for (Button btn : buttons) btn.setDisable(true);
        quizManager.showResult(mControllerVar.isCorrect, correctImg, wrongImg, resultImage, correctOrIncorrect_text);
        UIManger.crossFade(mControllerVar.isCorrect ? correctImg : wrongImg, quizManager, resultImage, buttons, this::showQuestion, mControllerVar);
    }

    public QuizManager getQuizManager() {return quizManager;}
    public void setTransitionRunning(boolean transitionRunning) {mControllerVar.transitionRunning = transitionRunning;}
    public boolean isTransitionRunning() {return mControllerVar.transitionRunning;}
    public MControllerVar getMControllerVar() {return mControllerVar;}

    public void showFinalScore(ActionEvent event) throws Exception {
         FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/percentHandler.fxml")
        );

        Parent root = loader.load();

        PercentHandler percentHandler = loader.getController();
        percentHandler.setData(mControllerVar, quizManager);

        // Get current stage from button click
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML public void goBack(ActionEvent e) {
        if (quizManager.getCurrentIndex() > 0) {
            quizManager.setIndex(quizManager.getCurrentIndex() - 1);
            showQuestion();
        }
    }

}