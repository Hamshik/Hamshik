package in.hamshik;


import java.util.List;

public class QuizManager {

    private final List<QuizQuestion> questions;
    private int currentIndex = 0;
    private int score;

    public QuizManager(List<QuizQuestion> questions) {
        this.questions = questions;
        this.score = questions.size();
    }

    public QuizQuestion getCurrentQuestion() {
        return questions.get(currentIndex);
    }

    public boolean checkAnswer(int selectedIndex) {
        boolean isCorrect = selectedIndex == getCurrentQuestion().getAnswerIndex();
        if (!isCorrect) score--;
        return isCorrect;
    }

    public boolean hasNext() {
        return currentIndex < questions.size() - 1;
    }

    public void nextQuestion() {
        if (hasNext()) currentIndex++;
    }

    public int getScore() { return score; }
    public int getCurrentIndex() { return currentIndex; }
    public int getTotalQuestions() { return questions.size(); }
}
