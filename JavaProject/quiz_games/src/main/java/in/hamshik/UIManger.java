package in.hamshik;

import java.util.List;

import javafx.scene.control.Button;


public class UIManger {

    
    public static void runNextQues(QuizManager quizManager, 
        List<Button> buttons, Runnable showQuestion, MControllerVar mControllerVar) {
        mControllerVar.transitionRunning = true;
        quizManager.nextQuestion();
        showQuestion.run();
        for (Button btn : buttons) 
            btn.setDisable(false);
        mControllerVar.transitionRunning = false;
    }


    public static void resetChoiceButtons(List<Button> buttons) {

        for (Button btn : buttons) {
            btn.getStyleClass().removeAll("activeBut", "inactiveBut");
            btn.getStyleClass().add("inactiveBut");
            btn.setDisable(false);
        }
    }
}
