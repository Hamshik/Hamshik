package in.hamshik;


import java.util.List;

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

    public boolean checkAnswer(String userAns) {
        boolean isCorrect = (userAns.equals(getCurrentQuestion().answer()));
        if (!isCorrect) score--;
        return isCorrect;
    }

    public QuizEntry getCurrentQuestion() {return questions.get(currentIndex);}
    public boolean hasNext() {return currentIndex < questions.size() - 1;}
    public void nextQuestion() {if (hasNext()) currentIndex++;}
    public int getScore() { return score; }
    public int getCurrentIndex() { return currentIndex + 1; }
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
}
