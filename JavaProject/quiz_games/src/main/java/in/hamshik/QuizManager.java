package in.hamshik;


import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class QuizManager {

    private final List<QuizEntry> questions;
    private int currentIndex = 0;
    private int score;

    public QuizManager(List<QuizEntry> questions) {
        this.questions = questions;
        this.score = questions.size();
    }

    public boolean checkAnswer(String userAns, int currentIndex) {
        boolean isCorrect = (userAns.equals(getCurrentQuestion(currentIndex).answer()));
        if (!isCorrect) score--;
        return isCorrect;
    }

    public QuizEntry getCurrentQuestion(int currentIndex) {return questions.get(currentIndex);}
    public boolean hasNext() {return currentIndex < questions.size() - 1;}
    public void nextQuestion() {if (hasNext()) currentIndex++;}
    public void setIndex(int index) {this.currentIndex = index;}
    public int getScore() { return score; }
    public int getCurrentIndex() { return currentIndex; }
    public int getTotalQuestions() { return questions.size(); }

    public void showResult(boolean isCorrect, Image correctImg, Image wrongImg, ImageView resultImage,
        Text correctOrIncorrect_text)
        {
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

    public void handleAns(ActionEvent e, String userAnswer, MControllerVar mControllerVar, int currentIndex, List<UserAnsEntry> userAnsEntry) {
        for (Button btn : mControllerVar.buttons) {
            btn.getStyleClass().removeAll("activeBut", "inactiveBut");
            btn.getStyleClass().add("inactiveBut");
            btn.setDisable(false);
        }

        Button btn = (Button) e.getSource();
        mControllerVar.userAnswer = btn.getText().substring(3);
        userAnsEntry.add(new UserAnsEntry(currentIndex, userAnswer));
        mControllerVar.shouldGONext = true;

        btn.getStyleClass().remove("inactiveBut");
        btn.getStyleClass().add("activeBut");
    }
}